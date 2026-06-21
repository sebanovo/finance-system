# Módulo de Reportes (reescritura)

Reescritura del módulo de reportes con **dos rieles** sobre un modelo
**multi-tenant por esquema** (`tenant_<slug>`, `search_path`, `public`/`reporting`
para lo global). Reemplaza al módulo viejo `modules/tenant/reporting` (eliminado).

- **Riel 1 — Controlados:** catálogo declarativo (`ReportDefinitionRegistry`),
  `sourceView` + filtros tipados.
- **Riel 2 — IA:** texto/voz → SQL generado por el microservicio `reports-ai`,
  validado por `AiSqlGuard` y ejecutado read-only.

## Arquitectura

```
finance-web (Angular)  ──HTTPS (JWT [+ X-Tenant-Slug en rutas tenant])──┐
                                                                          ▼
finance-api (Spring Boot) = GUARDIÁN
  Riel 1: ReportDefinitionRegistry + ReportSqlBuilder
  Riel 2: ReportsAiGateway → AiSqlGuard (JSqlParser)
  ReadOnlySqlExecutor (SET LOCAL search_path + READ ONLY tx + statement_timeout)
  Exporters PDF/XLSX · report_executions/report_exports (public) · Auditoría
  Rutas: /api/reports/** (tenant) · /api/platform/reports/** (platform)
        │                                              │ HTTP interno (token)
  readonly datasources (2 roles)                        ▼
        ▼                                       reports-ai (FastAPI) = CEREBRO
PostgreSQL                                        STT (Groq) · NL→SQL (LLM)
  tenant_<slug>.reporting_*  (vistas enmascaradas)   SIN acceso a la BD
  reporting.*  (UNION cross-tenant + tenant_slug)
  finance_tenant_readonly / finance_platform_readonly: SELECT solo en vistas
```

## Capa de seguridad (BD)

- **Roles read-only** `finance_tenant_readonly` / `finance_platform_readonly`:
  SELECT **solo sobre vistas `reporting_*`**, nunca sobre tablas crudas. Sin DML/DDL.
- **Vistas `reporting_*`** por tenant + schema `reporting` (cross-tenant via
  `reporting.regenerate_views()` con `UNION ALL` + `tenant_slug`).
- Grants + `regenerate_views()` se enganchan en provisión de tenant
  (`CreateTenantUseCase`), backfill de arranque (`PlatformBootstrapRunner`) y
  restore (`BackupJobAsyncExecutor`) — ver `common/tenancy/reporting/ReportingSecurityService`.
- **`AiSqlGuard`** recorre TODO el árbol SQL (FROM, JOIN, subqueries, CTEs, UNION,
  args de funciones): una sola SELECT, sin DML/multi-statement/`SELECT INTO`,
  sin `pg_catalog`/`information_schema`, sin schema-qualified en TENANT, whitelist
  de vistas, y LIMIT (respeta el del usuario o inyecta el del sistema).

## Endpoints

**Tenant** (requieren `X-Tenant-Slug`, permiso `reports.tenant.*`):
```
GET  /api/reports/definitions
POST /api/reports/run/{key}
POST /api/reports/ai/text
POST /api/reports/ai/voice          (multipart: audio)
GET  /api/reports/executions?page=&size=
GET  /api/reports/executions/{id}
POST /api/reports/executions/{id}/rerun
POST /api/reports/executions/{id}/exports   { "format": "PDF" | "XLSX" }
GET  /api/reports/exports/{exportId}/download
```
**Platform** (SuperAdmin, `@authorizationGuards.isPlatformAdmin()`): idéntico bajo
`/api/platform/reports/**`.

## Catálogo

11 reportes tenant + 8 platform (ver `ReportDefinitionRegistry`). Agregar un
reporte = verificar columnas + 1 vista (migración tenant o public) + 1 entrada en
el registry + whitelist. Las vistas tenant se otorgan solas; las platform
cross-tenant van en `regenerate_views()`, las que leen `public` directo son
vistas estáticas.

## Variables de entorno

`REPORTS_AI_MODE` (mock|http), `REPORTS_AI_BASE_URL`, `REPORTS_AI_INTERNAL_TOKEN`,
`REPORTS_AI_TIMEOUT_MS`, `REPORTS_AI_RATE_LIMIT`, `REPORTS_AI_AUDIO_MAX_BYTES`,
`AI_SQL_MAX_ROWS`, `AI_SQL_TIMEOUT_MS`, `REPORT_SNAPSHOT_MAX_ROWS`,
`REPORT_SNAPSHOT_MAX_BYTES`, `REPORT_EXPORT_MAX_ROWS`, `REPORT_EXPORT_STORAGE_PATH`,
`REPORTING_SCHEMA`, `TENANT_READONLY_DB_USER/PASSWORD`,
`PLATFORM_READONLY_DB_USER/PASSWORD`.

## Endurecimiento

- **Rate limit IA** por usuario (`AiRateLimiter`, en memoria) → 429.
- **Validación de audio** (tamaño/MIME) del lado Spring y del microservicio.
- **Auditoría:** `REPORT_EXECUTED` / `REPORT_FAILED` / `REPORT_EXPORTED` vía
  `AuditTrailService` (tenant o platform según scope).
- **Timeouts:** cliente IA con timeout corto; `statement_timeout` por ejecución;
  503 controlado si `reports-ai` está caído (los reportes controlados siguen).

## Prueba E2E (local)

Levantar la base + API (corre migraciones `public V9–V13` / `tenant V16–V19` y el
backfill de grants), y opcionalmente el cerebro:

```bash
docker compose up -d serv-finance-db
docker compose up reports-ai          # opcional; con REPORTS_AI_MODE=http
# arrancar finance-api (perfil dev)
```

Verificar el aislamiento de la BD:
```bash
Get-Content scripts/reporting-security-verification.sql \
  | docker compose exec -T serv-finance-db psql -U finance_user -d finance_db
```

### Tenant: controlado → export Excel
```bash
TENANT=acme
TOKEN=...   # JWT de un usuario del tenant con reports.tenant.*

# 1) ejecutar
EXEC=$(curl -s -X POST localhost:8080/api/reports/run/TENANT_MOVEMENTS \
  -H "Authorization: Bearer $TOKEN" -H "X-Tenant-Slug: $TENANT" \
  -H 'Content-Type: application/json' -d '{"params":{"status":"COMPLETED"}}' \
  | jq -r '.data.executionId')

# 2) export XLSX
EXP=$(curl -s -X POST localhost:8080/api/reports/executions/$EXEC/exports \
  -H "Authorization: Bearer $TOKEN" -H "X-Tenant-Slug: $TENANT" \
  -H 'Content-Type: application/json' -d '{"format":"XLSX"}' | jq -r '.data.id')

# 3) descargar
curl -s -L localhost:8080/api/reports/exports/$EXP/download \
  -H "Authorization: Bearer $TOKEN" -H "X-Tenant-Slug: $TENANT" -o reporte.xlsx
```

### Tenant: IA (texto) → tabla
```bash
curl -s -X POST localhost:8080/api/reports/ai/text \
  -H "Authorization: Bearer $TOKEN" -H "X-Tenant-Slug: $TENANT" \
  -H 'Content-Type: application/json' -d '{"prompt":"movimientos recientes"}' | jq '.data.rowCount'
```

### Platform: comparativa cross-tenant → export PDF
```bash
PTOKEN=...  # JWT de SuperAdmin

EXEC=$(curl -s -X POST localhost:8080/api/platform/reports/run/PLATFORM_TENANT_MOVEMENT_RANKING \
  -H "Authorization: Bearer $PTOKEN" -H 'Content-Type: application/json' -d '{}' \
  | jq -r '.data.executionId')

EXP=$(curl -s -X POST localhost:8080/api/platform/reports/executions/$EXEC/exports \
  -H "Authorization: Bearer $PTOKEN" -H 'Content-Type: application/json' -d '{"format":"PDF"}' \
  | jq -r '.data.id')

curl -s -L localhost:8080/api/platform/reports/exports/$EXP/download \
  -H "Authorization: Bearer $PTOKEN" -o ranking.pdf
```

En el frontend: `/dashboard/reporting` (tenant) y `/platform/reporting` (SuperAdmin).
