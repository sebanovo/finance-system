
# Frontend SuperAdmin - Master Implementation Plan

## Objetivo

Completar el módulo SuperAdmin del frontend siguiendo un orden de implementación basado en dependencias técnicas, evitando construir funcionalidades de negocio sobre una base inestable de autenticación, sesión, contratos HTTP o arquitectura.

---

# 1. Estado Actual

## Backend disponible

- Platform Authentication
- Platform Dashboard
- Platform Tenants
- Platform Plans
- Platform Subscriptions
- Platform Backups
- Platform Audit
- Platform Superadmins

## Frontend disponible

- Login plataforma (parcial)
- Dashboard (estructura básica)
- Tenants (parcial)
- Plans (parcial)
- Subscriptions (parcial)
- Audit (parcial)

## Frontend faltante

- Refresh token completo
- Dashboard conectado al backend
- Backups completos
- Detail pages
- Security pages
- Profile pages
- Arquitectura modular completa

---

# 2. Principios de Arquitectura

## Regla principal

Nunca desarrollar módulos operativos antes de completar:

1. Autenticación
2. Sesión
3. Guards
4. Interceptor
5. Contratos TypeScript
6. Servicios

---

# 3. Separación Platform vs Tenant

## Platform

Responsable de:

- Tenants
- Plans
- Subscriptions
- Backups
- Audit
- Dashboard Global

Endpoints:

- /api/platform/**

## Tenant

Responsable de:

- Users
- Accounts
- Transactions
- FX
- Limits
- Accounting

Endpoints:

- /api/**

## Regla

No compartir tokens entre contextos.

---

# 4. Arquitectura Compartida

## Componentes reutilizables

- ApiResponse<T>
- ApiError Handling
- Interceptor Base
- Auth Session Base
- Toasts
- Dialogs
- Loading States

## Componentes específicos

### Platform

- PlatformAuthService
- PlatformDashboardService
- PlatformTenantService
- PlatformPlanService
- PlatformSubscriptionService
- PlatformBackupService
- PlatformAuditService

### Tenant

Se implementará posteriormente.

---

# 5. Fase 1 - Seguridad y Sesión

## Objetivo

Estabilizar completamente la autenticación de plataforma.

## Tareas

### PlatformAuthService

Implementar:

- login()
- refreshToken()
- getMe()
- logout()
- changePassword()

### Modelos

- PlatformLoginRequest
- PlatformAuthTokenResponse
- PlatformRefreshTokenRequest
- PlatformChangePasswordRequest
- AuthenticatedPlatformSuperadminResponse

### PlatformStorageService

Implementar:

- saveAccessToken()
- saveRefreshToken()
- getAccessToken()
- getRefreshToken()
- clearSession()

### PlatformAuthGuard

Validaciones:

- token existente
- sesión válida
- refresh automático
- redirección a login

### Interceptor

Agregar:

- refresh automático
- retry request
- logout forzado si refresh falla

## Definition of Done

- Login funcional
- Logout funcional
- Refresh funcional
- Session recovery funcional

---

# 6. Fase 2 - Refactor Arquitectura

## Objetivo

Eliminar PlatformService monolítico.

## Crear módulos

- platform-auth
- platform-dashboard
- platform-tenants
- platform-plans
- platform-subscriptions
- platform-backups
- platform-audit
- platform-superadmins

## Resultado esperado

Cada módulo posee:

- models
- api
- usecases
- ui

---

# 7. Fase 3 - Dashboard

## Endpoint

GET /api/platform/dashboard/summary

## Implementar

### Modelos

- SuperadminDashboardResponse
- DashboardSummary
- DashboardAlerts
- DashboardInsights
- DashboardActivity

### Servicios

- PlatformDashboardService

### Use Cases

- PlatformDashboardSummaryUseCase

### UI

- KPIs
- Tenants
- Plans
- Subscriptions
- Users
- Alerts
- Activity
- Audit Summary

## Definition of Done

Dashboard completamente alimentado desde backend.

---

# 8. Fase 4 - Tenants

## Endpoints

- GET tenants
- GET tenant by id
- POST tenant
- Activate tenant
- Deactivate tenant

## Implementar

### Pages

- PlatformTenantsPage
- PlatformTenantDetailPage

### Use Cases

- List
- Detail
- Create
- Activate
- Deactivate

### Funcionalidades

- filtros
- búsqueda
- estados
- acciones

## Definition of Done

Administración completa de tenants.

---

# 9. Fase 5 - Plans

## Endpoints

- List
- Detail
- Create
- Activate
- Deactivate

## Pages

- PlatformPlansPage
- PlatformPlanDetailPage

## Funcionalidades

- CRUD básico
- activación
- desactivación
- visualización de tenants asociados

---

# 10. Fase 6 - Subscriptions

## Endpoints

- List
- Detail
- Assign

## Pages

- PlatformSubscriptionsPage
- PlatformSubscriptionDetailPage

## Funcionalidades

- selección de tenant
- selección de plan
- override trial

## Regla

No usar texto libre cuando existan catálogos.

---

# 11. Fase 7 - Backups

## Estado actual

No implementado en frontend.

## Endpoints

- Create Full Backup
- Create Tenant Backup
- List Backups
- Backup Detail
- Download Backup
- Restore Backup

## Implementar

### Models

- BackupJobResponse
- CreateBackupRequest
- RestoreBackupRequest

### Pages

- PlatformBackupsPage
- PlatformBackupDetailPage

### Use Cases

- List
- Detail
- Download
- Restore
- Create Full
- Create Tenant

## Seguridad

Restauración requiere confirmación explícita.

Texto obligatorio:

RESTORE

---

# 12. Fase 8 - Auditoría

## Endpoint

GET /api/platform/audit/events

## Mejoras

### Modelo

Agregar:

- actorEmail
- tenantSlug
- ipAddress
- userAgent
- requestId
- correlationId
- source
- outcome
- beforeState
- afterState

### UI

- filtros
- búsqueda
- detalle
- visualización JSON

---

# 13. Fase 9 - Perfil y Seguridad

## Pages

### Profile

- email
- nombre
- apellido
- estado

### Security

- change password
- logout

## Endpoints

- getMe
- changePassword
- logout

---

# 14. UX Standards

## Estados obligatorios

- idle
- loading
- success
- error
- empty

## Componentes

- Skeletons
- Empty States
- Error States
- Toasts
- Confirm Dialogs

---

# 15. Acciones Destructivas

Requieren confirmación:

- Deactivate Tenant
- Deactivate Plan
- Restore Backup
- Logout

---

# 16. Roadmap

## Sprint 1

- Auth
- Storage
- Guard
- Refresh
- Interceptor

## Sprint 2

- Refactor servicios
- Modelos
- Estados
- Error Handling

## Sprint 3

- Dashboard completo

## Sprint 4

- Tenants
- Plans
- Subscriptions

## Sprint 5

- Backups

## Sprint 6

- Audit

## Sprint 7

- Profile
- Security
- UX

---

# 17. Riesgos Técnicos

## Alto

- Refresh token incorrecto
- Mezcla platform/tenant
- Contratos backend desactualizados

## Medio

- Estados inconsistentes
- Paginación futura

## Bajo

- Responsive
- Temas visuales

---

# 18. Definition of Done Final

El módulo SuperAdmin estará terminado cuando:

- Todos los endpoints platform estén integrados.
- No existan any en servicios platform.
- Dashboard use backend real.
- Backups funcionen completamente.
- Auditoría tenga detalle completo.
- Existan páginas Profile y Security.
- Session recovery funcione automáticamente.
- Todos los módulos manejen loading/error/empty.
- No existan dependencias pendientes con tenant.
