# Sistema de Gestión de Finanzas SaaS — Base actual del backend

## 1. Resumen del proyecto

Este proyecto corresponde a un **Sistema de Gestión de Finanzas** con enfoque **SaaS multitenant**, El objetivo general del proyecto es desarrollar una plataforma que permita a múltiples organizaciones registrar, organizar, controlar y analizar su información económica desde una misma solución tecnológica, con aislamiento lógico de datos, seguridad y escalabilidad. La documentación del proyecto plantea explícitamente el enfoque SaaS, la multitenencia, la gestión de usuarios y control de acceso, la administración de información financiera, reportes, acceso multiplataforma y seguridad avanzada.

Además, el perfil inicial del proyecto ya definía la idea base de una solución financiera tipo wallet/sistema financiero para llevar control de gastos, ingresos y transacciones, incluyendo autenticación de usuarios y visión del saldo o estado financiero.

---

## 2. Enfoque actual del backend

La base actual no es solamente un backend CRUD. Está pensada como una **plataforma SaaS con dos entradas principales**:

1. **Portal de plataforma**
   - para el **superadmin**
   - administra planes, tenants, auditoría global, suscripciones y dashboard global

2. **Portal público / onboarding**
   - para una persona externa que quiere registrarse
   - crea su tenant
   - recibe plan DEMO automático
   - se convierte en `OWNER_ADMIN` de su tenant

3. **Portal del tenant**
   - para usuarios del tenant
   - login por tenant
   - dashboard tenant
   - gestión de usuarios, roles, configuración y suscripción actual
   - sujeto a límites del plan

---

## 3. Arquitectura base actual

### 3.1 Stack técnico actual

- **Java 21**
- **Spring Boot 3.5**
- **PostgreSQL**
- **Flyway**
- **JWT**
- **Docker / Docker Compose**
- **OpenAPI / Swagger**
- **Mailpit** para pruebas de correo local

### 3.2 Estilo arquitectónico

La organización sigue una **hexagonal simplificada con vertical slice**, pensada para ser:

- escalable
- clara
- mantenible
- entendible

Se evita una estructura excesivamente compleja con demasiados `inbound/outbound/ports`, pero se mantiene una separación fuerte entre responsabilidades.

#### Estructura general conceptual

```text
common/
modules/
  platform/
  identity/
  governance/
  dashboard/
  finance/   (pendiente)
```

#### Dentro de cada módulo

```text
domain/
application/
infrastructure/
```

### 3.3 Qué significa cada capa

#### `common`

Contiene lo transversal y técnico:

- configuración
- seguridad
- JWT
- tenancy
- mail
- excepciones
- respuestas estándar
- utilidades

#### `modules`

Contiene la lógica de negocio agrupada por capacidades funcionales.

#### `domain`

- modelos del dominio
- contratos de repositorio
- excepciones de negocio

#### `application`

- DTOs
- mappers
- use cases
- servicios de aplicación

#### `infrastructure`

- controladores REST
- adaptadores JPA / JDBC
- persistencia concreta
- integración con infraestructura técnica

---

## 4. Multitenencia: cómo está resuelta

La plataforma usa **multitenencia por schemas en PostgreSQL**.

### 4.1 Esquema `public`

Representa la capa global de la plataforma SaaS.

Aquí viven tablas como:

- `platform_tenants`
- `platform_plans`
- `platform_subscriptions`
- `platform_superadmins`
- `system_permissions`
- `platform_audit_events`

### 4.2 Schemas por tenant

Cada tenant tiene su propio schema, por ejemplo:

- `tenant_financruz`
- `tenant_contanorte`

Dentro de cada schema tenant viven sus datos propios:

- `tenant_users`
- `tenant_roles`
- `tenant_user_roles`
- `tenant_role_permissions`
- `tenant_settings`
- `tenant_audit_events`
- `tenant_password_reset_tokens`

### 4.3 Resolución del tenant

El tenant se resuelve mediante el header:

```http
X-Tenant-Slug: financruz
```

El sistema:

1. toma el slug
2. lo normaliza
3. lo transforma en schema
4. trabaja con ese schema en la conexión

### 4.4 Rutas globales y tenant

- **rutas públicas** → no requieren token
- **rutas platform** → trabajan en `public`
- **rutas tenant** → trabajan dentro del schema del tenant resuelto

---

## 5. Seguridad actual

### 5.1 Modelo general

La seguridad es **stateless** con JWT:

- no se usan sesiones
- cada request protegida envía `Authorization: Bearer ...`

### 5.2 Qué contiene el token

Los JWT actuales incluyen:

- subject (email)
- tenant slug
- roles
- tipo de token (access/refresh)

### 5.3 Separación de auth

Actualmente existen dos autenticaciones separadas:

#### `platform/auth`

para el superadmin de la plataforma

#### `identity/auth`

para usuarios tenant

Eso evita mezclar:

- auth de plataforma
- auth de tenant

### 5.4 Seguridad de plataforma

Los endpoints de plataforma se protegen con guards tipo:

- `isPlatformAuthenticated()`
- `isPlatformAdmin()`

Así, un token tenant no debería poder entrar a endpoints platform.

---

## 6. Módulos implementados actualmente

## 6.1 `platform/auth`

Responsabilidad:

- login real del superadmin
- refresh
- me
- change password
- logout

Permite que el superadmin use el portal de plataforma sin depender del `dev-token`.

## 6.2 `platform/superadmin`

Responsabilidad:

- manejo del actor superadmin de la plataforma
- perfil actual del superadmin autenticado

## 6.3 `platform/plans`

Responsabilidad:

- gestión de planes globales del SaaS

Actualmente soporta:

- crear plan
- listar planes
- ver plan por id
- activar/desactivar plan

Campos conceptuales actuales del plan:

- `code`
- `name`
- `description`
- `maxUsers`
- `maxRoles`
- `planType`
- `trialDays`
- `active`

Planes sembrados en bootstrap:

- `DEMO`
- `BASIC`
- `PRO`
- `ENTERPRISE`

## 6.4 `platform/tenants`

Responsabilidad:

- gestión global de tenants

Actualmente soporta:

- crear tenant
- listar tenants
- obtener tenant por id
- activar/desactivar tenant

Cuando crea un tenant:

1. normaliza slug
2. crea schema tenant
3. ejecuta migraciones tenant
4. ejecuta bootstrap tenant
5. crea suscripción actual

## 6.5 `platform/subscriptions`

Responsabilidad:

- modelar la suscripción real del tenant

Actualmente soporta:

- asignar suscripción actual
- trial demo de 10 días
- plan actual por tenant
- consulta global de suscripciones
- consulta de suscripción actual desde tenant
- refresco del estado `EXPIRED` cuando corresponde

Estados actuales:

- `TRIAL`
- `ACTIVE`
- `EXPIRED`
- `CANCELLED`

## 6.6 `platform/onboarding`

Responsabilidad:

- signup público

Actualmente permite:

- que una persona externa cree su tenant
- cree su usuario principal
- reciba plan `DEMO`
- quede con rol `OWNER_ADMIN`

## 6.7 `platform/tenantsettings`

Responsabilidad:

- configuración del tenant dentro de su propio schema

Actualmente cubre:

- lectura de settings
- actualización de settings
- datos como nombre, timezone, currency, etc.

## 6.8 `identity/users`

Responsabilidad:

- gestión de usuarios del tenant

Actualmente cubre:

- crear usuario
- listar usuarios
- obtener usuario por id
- actualizar usuario
- activar/desactivar usuario

Con enforcement actual por plan:

- crear y activar usuarios depende del límite del plan

## 6.9 `identity/access`

Responsabilidad:

- roles y permisos del tenant

Actualmente cubre:

- listar permisos del sistema
- crear roles del tenant
- listar roles
- obtener rol por id
- actualizar rol
- activar/desactivar rol
- asignar roles a usuario
- consultar roles de usuario

Con enforcement actual por plan:

- crear y activar roles personalizados depende del límite del plan

### Roles base del tenant sembrados por bootstrap

- `OWNER_ADMIN`
- `ADMIN`
- `USER`

Los roles base no cuentan dentro del límite de roles personalizados del plan.

## 6.10 `identity/auth`

Responsabilidad:

- autenticación del usuario tenant

Actualmente cubre:

- login
- refresh
- me
- logout
- change password
- forgot password
- reset password

## 6.11 `governance/notifications`

Responsabilidad:

- correo saliente
- notificaciones relacionadas con auth

Actualmente soporta:

- forgot password
- reset password
- envío de correo vía Mailpit en desarrollo

## 6.12 `governance/audit`

Responsabilidad:

- trazabilidad

Tipos de auditoría:

- `platform_audit_events`
- `tenant_audit_events`

Se registran eventos como:

- login
- refresh
- logout
- password change
- forgot/reset password
- create/update user
- create/update role
- tenant created
- subscription assigned
- public signup completed

## 6.13 `dashboard/platform`

Responsabilidad:

- resumen global para el superadmin

Actualmente expone métricas como:

- total tenants
- tenants activos
- tenants inactivos
- trial tenants
- paid active tenants
- expired tenants
- tenants sin suscripción actual
- distribución por plan
- trials que vencen en los próximos 7 días

## 6.14 `dashboard/tenant`

Responsabilidad:

- resumen del estado del tenant actual

Actualmente expone:

- tenant actual
- estado tenant
- plan actual
- estado suscripción
- trial / expiración
- usuarios activos vs máximo
- roles personalizados activos vs máximo
- alertas
- flags `canCreateUsers` y `canCreateRoles`

---

## 7. Flujo funcional actual del producto

### 7.1 Flujo del superadmin

1. inicia sesión en `/api/platform/auth/login`
2. accede al dashboard global
3. administra:
   - tenants
   - planes
   - suscripciones
   - auditoría
   - estado global del SaaS

### 7.2 Flujo del usuario externo

1. entra al signup público
2. crea su tenant
3. recibe plan DEMO
4. se convierte en `OWNER_ADMIN`
5. luego hace login tenant normal

### 7.3 Flujo del tenant

1. hace login en `/api/auth/login` con `X-Tenant-Slug`
2. entra a su dashboard tenant
3. opera dentro de los límites del plan

---

## 8. Regla funcional clave del sistema

La base actual ya separa correctamente dos conceptos:

### Rol

Define **qué puede intentar hacer**.

### Plan

Define **hasta dónde el sistema le permite operar**.

Ejemplo:

- el `OWNER_ADMIN` puede ser administrador del tenant
- pero si el plan DEMO permite solo 2 usuarios, no podrá crear un tercero

Esto es clave para el modelo SaaS.

---

## 9. Estado actual del plan DEMO

Plan DEMO actual:

- `trialDays = 10`
- `maxUsers = 2`
- `maxRoles = 2` (roles personalizados)
- acceso al dashboard tenant
- acceso a configuración básica
- acceso limitado por enforcement

---

## 10. Guía completa Postman

## 10.1 Variables recomendadas del Environment

Environment con:

```text
baseUrl = http://localhost:8080

platformSuperadminEmail = superadmin@finance.local
platformSuperadminPassword = SuperAdmin123!

tenantSlug = financruz
tenantAdminEmail = admin@financruz.com
tenantAdminPassword = Password123!

platformAccessToken =
platformRefreshToken =

tenantAccessToken =
tenantRefreshToken =

tenantId =
planId =
subscriptionId =
userId =
roleId =
resetToken =
```

## 10.2 Headers base

### Para platform

```http
Authorization: Bearer {{platformAccessToken}}
```

### Para tenant

```http
Authorization: Bearer {{tenantAccessToken}}
X-Tenant-Slug: {{tenantSlug}}
```

## 10.3 Orden recomendado de carpetas en Postman

```text
01 Public
02 Platform Auth
03 Platform Plans
04 Platform Tenants
05 Platform Subscriptions
06 Platform Dashboard
07 Tenant Auth
08 Tenant Subscription
09 Tenant Dashboard
10 Tenant Settings
11 Tenant Users
12 Tenant Access
13 Password Recovery
14 Tenant Audit
15 Platform Audit
```

---

# 11. Endpoints listos para probar

## 11.1 Públicos

### `GET /api/public/ping`

Sirve para verificar que la API responde.

### `GET /api/public/security-status`

Sirve para verificar estado básico de seguridad/configuración pública.

### `POST /api/public/signup`

Sirve para registro público:

- crea tenant
- crea schema
- crea suscripción DEMO
- crea owner admin

Body ejemplo:

```json
{
  "companyName": "FinanCruz Ltda",
  "tenantSlug": "financruz",
  "adminEmail": "admin@financruz.com",
  "password": "Password123!",
  "firstName": "Carlos",
  "lastName": "Rojas"
}
```

---

## 11.2 Platform Auth

### `POST /api/platform/auth/login`

Login real del superadmin.

Body:

```json
{
  "email": "{{platformSuperadminEmail}}",
  "password": "{{platformSuperadminPassword}}"
}
```

### `POST /api/platform/auth/refresh`

Renueva token platform.

Body:

```json
{
  "refreshToken": "{{platformRefreshToken}}"
}
```

### `GET /api/platform/auth/me`

Devuelve el superadmin autenticado.

### `POST /api/platform/auth/change-password`

Cambia contraseña del superadmin.

Body:

```json
{
  "currentPassword": "SuperAdmin123!",
  "newPassword": "SuperAdmin999!"
}
```

### `POST /api/platform/auth/logout`

Logout stateless del superadmin.

---

## 11.3 Platform Plans

### `POST /api/platform/plans`

Crea un plan.

Body ejemplo:

```json
{
  "code": "PREMIUM",
  "name": "Premium",
  "description": "Premium subscription plan",
  "maxUsers": 50,
  "maxRoles": 20,
  "planType": "PAID",
  "trialDays": null
}
```

### `GET /api/platform/plans`

Lista planes.

### `GET /api/platform/plans/{id}`

Obtiene un plan por id.

### `PATCH /api/platform/plans/{id}/activate`

Activa plan.

### `PATCH /api/platform/plans/{id}/deactivate`

Desactiva plan.

---

## 11.4 Platform Tenants

### `POST /api/platform/tenants`

Crea tenant manualmente desde plataforma.

Body ejemplo:

```json
{
  "name": "ContaNorte SRL",
  "slug": "contanorte",
  "planCode": "DEMO"
}
```

### `GET /api/platform/tenants`

Lista tenants.

### `GET /api/platform/tenants/{id}`

Obtiene tenant por id.

### `PATCH /api/platform/tenants/{id}/activate`

Activa tenant.

### `PATCH /api/platform/tenants/{id}/deactivate`

Desactiva tenant.

---

## 11.5 Platform Subscriptions

### `POST /api/platform/subscriptions/assign`

Asigna o cambia la suscripción actual de un tenant.

Body ejemplo:

```json
{
  "tenantId": "{{tenantId}}",
  "planCode": "BASIC",
  "overrideTrialDays": null
}
```

Body para extender demo:

```json
{
  "tenantId": "{{tenantId}}",
  "planCode": "DEMO",
  "overrideTrialDays": 10
}
```

### `GET /api/platform/subscriptions`

Lista suscripciones.

### `GET /api/platform/subscriptions/{id}`

Obtiene suscripción por id.

---

## 11.6 Platform Dashboard

### `GET /api/platform/dashboard/summary`

Devuelve resumen global de la plataforma:

- total tenants
- activos/inactivos
- demo/paid/expired
- tenants por plan
- trials próximos a vencer

---

## 11.7 Tenant Auth

### `POST /api/auth/login`

Login real del tenant.

Headers:

```http
X-Tenant-Slug: {{tenantSlug}}
```

Body:

```json
{
  "email": "{{tenantAdminEmail}}",
  "password": "{{tenantAdminPassword}}"
}
```

### `POST /api/auth/refresh`

Refresh token del tenant.

Headers:

```http
X-Tenant-Slug: {{tenantSlug}}
```

Body:

```json
{
  "refreshToken": "{{tenantRefreshToken}}"
}
```

### `GET /api/auth/me`

Devuelve usuario tenant autenticado.

### `POST /api/auth/change-password`

Cambia password del usuario tenant.

Body:

```json
{
  "currentPassword": "{{tenantAdminPassword}}",
  "newPassword": "Password999!"
}
```

### `POST /api/auth/logout`

Logout stateless del tenant.

---

## 11.8 Password recovery

### `POST /api/auth/forgot-password`

Inicia recuperación de contraseña.

Headers:

```http
X-Tenant-Slug: {{tenantSlug}}
```

Body:

```json
{
  "email": "{{tenantAdminEmail}}"
}
```

### `POST /api/auth/reset-password`

Resetea contraseña con token.

Headers:

```http
X-Tenant-Slug: {{tenantSlug}}
```

Body:

```json
{
  "token": "{{resetToken}}",
  "newPassword": "PasswordABC123!"
}
```

### Mailpit

Para ver correos en desarrollo:

- `http://localhost:8025`

---

## 11.9 Tenant Subscription

### `GET /api/subscription/current`

Devuelve la suscripción actual del tenant autenticado.

Sirve para ver:

- plan actual
- estado
- trial
- expiresAt
- remainingDays

---

## 11.10 Tenant Dashboard

### `GET /api/dashboard/tenant/summary`

Devuelve resumen del tenant actual:

- plan
- trial
- días restantes
- usuarios usados / máximo
- roles usados / máximo
- alertas
- flags de creación

---

## 11.11 Tenant Settings

### `GET /api/settings/tenant`

Obtiene configuración del tenant.

### `PUT /api/settings/tenant`

Actualiza configuración del tenant.

Body ejemplo:

```json
{
  "companyName": "FinanCruz Ltda",
  "legalName": "FinanCruz Cooperativa Financiera Ltda",
  "timezone": "America/La_Paz",
  "currency": "BOB",
  "contactEmail": "contacto@financruz.com",
  "contactPhone": "+59170000000"
}
```

---

## 11.12 Tenant Users

### `POST /api/users`

Crea usuario tenant.
Está limitado por el plan.

Body ejemplo:

```json
{
  "email": "usuario2@financruz.com",
  "password": "Password123!",
  "firstName": "Ana",
  "lastName": "Suarez"
}
```

### `GET /api/users`

Lista usuarios tenant.

### `GET /api/users/{id}`

Obtiene usuario tenant por id.

### `PUT /api/users/{id}`

Actualiza usuario tenant.

Body ejemplo:

```json
{
  "email": "usuario2@financruz.com",
  "firstName": "Ana Maria",
  "lastName": "Suarez"
}
```

### `PATCH /api/users/{id}/activate`

Activa usuario.
También está limitado por el plan.

### `PATCH /api/users/{id}/deactivate`

Desactiva usuario.

---

## 11.13 Tenant Access

### `GET /api/access/permissions`

Lista permisos base del sistema.

### `POST /api/access/roles`

Crea rol personalizado tenant.
Está limitado por el plan.

Body ejemplo:

```json
{
  "name": "CONTADOR",
  "description": "Rol contador",
  "permissionCodes": ["users.read", "tenant-settings.read", "audit.read"]
}
```

### `GET /api/access/roles`

Lista roles tenant.

### `GET /api/access/roles/{id}`

Obtiene rol tenant por id.

### `PUT /api/access/roles/{id}`

Actualiza rol.

### `PATCH /api/access/roles/{id}/activate`

Activa rol personalizado.
También limitado por plan.

### `PATCH /api/access/roles/{id}/deactivate`

Desactiva rol.

### `PUT /api/access/users/{userId}/roles`

Asigna roles a usuario.

Body:

```json
{
  "roleIds": ["{{roleId}}"]
}
```

### `GET /api/access/users/{userId}/roles`

Lista roles del usuario.

---

## 11.14 Tenant Audit

### `GET /api/audit/events?limit=50`

Lista eventos de auditoría del tenant.

---

## 11.15 Platform Audit

### `GET /api/platform/audit/events?limit=50`

Lista eventos de auditoría global de plataforma.

---

# 12. Flujos recomendados de prueba en Postman

## Flujo A — probar la plataforma SaaS completa desde cero

1. `POST /api/platform/auth/login`
2. `GET /api/platform/dashboard/summary`
3. `GET /api/platform/plans`
4. `POST /api/public/signup`
5. `GET /api/platform/tenants`
6. `GET /api/platform/subscriptions`
7. `GET /api/platform/audit/events`

## Flujo B — probar onboarding público + tenant

1. `POST /api/public/signup`
2. `POST /api/auth/login`
3. `GET /api/auth/me`
4. `GET /api/dashboard/tenant/summary`
5. `GET /api/subscription/current`
6. `GET /api/settings/tenant`

## Flujo C — probar enforcement del plan DEMO

1. signup público
2. login tenant
3. crear segundo usuario
4. intentar crear tercer usuario → debe fallar
5. crear 2 roles personalizados
6. intentar crear tercero → debe fallar
7. verificar dashboard tenant

## Flujo D — probar recuperación de contraseña

1. `POST /api/auth/forgot-password`
2. abrir Mailpit
3. copiar token
4. `POST /api/auth/reset-password`
5. volver a login tenant

---

# 13. Errores esperables y significado

## 400 Bad Request

- body inválido
- validación fallida
- slug inválido
- token reset inválido
- planType inválido

## 401 Unauthorized

- falta token
- token inválido
- token expirado
- login incorrecto

## 403 Forbidden

- autenticado pero sin permiso suficiente
- token tenant intentando entrar a portal platform

## 404 Not Found

- recurso inexistente
- tenant/plan/subscription/user/role no encontrado

## 409 / Business conflict

- email duplicado
- slug duplicado
- plan code duplicado
- role name duplicado

## Denegación por plan

- crear usuarios por encima del máximo
- crear roles personalizados por encima del máximo
- activar usuarios/roles excediendo límites

---

# 14. Qué ya está cubierto y qué falta

## Ya cubierto

- base SaaS multitenant
- superadmin auth real
- onboarding público
- tenant auth real
- suscripciones
- trial demo
- enforcement por plan
- dashboard platform
- dashboard tenant
- auditoría
- recuperación de contraseña

## Lo siguiente natural

A partir de aquí, lo más lógico sería entrar al dominio financiero propiamente dicho:

1. `finance/accounts`
2. `finance/categories`
3. `finance/transactions`
4. `finance/cashbox`
5. `finance/reports`

---

## 15. Uso académico sugerido de este documento

Este archivo puede servir como:

- README técnico del backend
- base de documentación de arquitectura
- guía de pruebas para Postman
- apoyo para exposición o defensa del proyecto
- checklist de avance funcional actual

---

## 15. Saludos :B
