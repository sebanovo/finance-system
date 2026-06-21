# 📁 Documento de contexto del proyecto `finance-web`

## 1. Resumen ejecutivo

Este es un **SaaS financiero multi-tenant** con dos niveles de acceso:

- **Platform (SuperAdmin)**: Administra tenants, planes y la plataforma en general.
- **Tenant**: Cada empresa tiene su propio espacio aislado con sus usuarios, roles y datos financieros.

La aplicación está construida con **Angular** siguiendo una arquitectura **Feature-Sliced Design (FSD)** sobre principios de **Arquitectura Hexagonal**.

---

## 2. Arquitectura del frontend

### 2.1 Estructura general

```
src/app/
├── entities/       # Modelos de datos y servicios API
├── features/       # Funcionalidades independientes (slices FSD)
├── pages/          # Componentes de nivel de página
├── shared/         # Código reutilizable (guards, interceptors, UI)
├── widgets/        # Componentes de UI específicos de la app
```

### 2.2 Organización por features (FSD)

Cada feature sigue el patrón:

```
feature-name/
├── application/      # Casos de uso (usecases)
├── ui/               # Componentes de UI específicos
└── lib/              # Lógica compartida de la feature (opcional)
```

### 2.3 Features ya implementadas

| Feature | Descripción |
|---------|-------------|
| `auth` | Login de tenant |
| `onboarding` | Registro público (signup) |
| `dashboard` | Resumen del tenant |
| `user-management` | CRUD de usuarios del tenant |
| `role-management` | CRUD de roles y permisos del tenant |
| `account-management` | Gestión de cuentas (wallets) |
| `transactions-management` | Gestión de transacciones |
| `accounting` | Contabilidad básica |
| `fx-management` | Tipos de cambio y comisiones |
| `limits-management` | Reglas de límites por plan |
| `user-role-management` | Asignación de roles a usuarios |

---

## 3. Modelo de roles (tenant)

| Rol | Descripción |
|-----|-------------|
| `OWNER_ADMIN` | Dueño del tenant (creado en signup) |
| `ADMIN` | Administrador del tenant |
| `USER` | Usuario normal |
| `AUDITOR` | Solo lectura |
| `CONTADOR` | Acceso contable |
| `SOPORTE` | Atención al cliente |

---

## 4. Modelo de planes (SaaS)

El sistema actual tiene planes que limitan:

- `maxUsers`: Máximo de usuarios del tenant
- `maxRoles`: Máximo de roles personalizados
- `trialDays`: Días de prueba (para plan DEMO)

### Plan DEMO actual:

| Parámetro | Valor |
|-----------|-------|
| `trialDays` | 10 |
| `maxUsers` | 2 |
| `maxRoles` | 2 |

---

## 5. Backend disponible

- API REST con **Spring Boot**
- Endpoints públicos (`/api/public/`)
- Endpoints de platform (`/api/platform/`)
- Endpoints de tenant (`/api/...` con header `X-Tenant-Slug`)

---

## 6. 🚨 LO QUE FALTA IMPLEMENTAR (PRIORIDAD ALTA)

### 6.1 Módulo Platform (SuperAdmin)

El frontend actual **NO TIENE** implementado el módulo de administración de la plataforma. El backend ya tiene los endpoints listos.

#### 📋 Pendiente:

| Módulo | Endpoints pendientes | UI necesaria |
|--------|---------------------|--------------|
| **Platform Auth** | `POST /api/platform/auth/login` | Página de login de superadmin |
| | `POST /api/platform/auth/refresh` | (background) |
| | `GET /api/platform/auth/me` | Header/drawer con info |
| | `POST /api/platform/auth/change-password` | Modal |
| | `POST /api/platform/auth/logout` | Botón cerrar sesión |
| **Platform Plans** | `POST /api/platform/plans` | Formulario de creación |
| | `GET /api/platform/plans` | Tabla de planes |
| | `GET /api/platform/plans/{id}` | Detalle |
| | `PATCH /api/platform/plans/{id}/activate` | Botón activar |
| | `PATCH /api/platform/plans/{id}/deactivate` | Botón desactivar |
| **Platform Tenants** | `POST /api/platform/tenants` | Formulario de creación manual |
| | `GET /api/platform/tenants` | Tabla de tenants |
| | `GET /api/platform/tenants/{id}` | Detalle |
| | `PATCH /api/platform/tenants/{id}/activate` | Botón activar |
| | `PATCH /api/platform/tenants/{id}/deactivate` | Botón desactivar |
| **Platform Subscriptions** | `POST /api/platform/subscriptions/assign` | Asignar plan a tenant |
| | `GET /api/platform/subscriptions` | Lista de suscripciones |
| | `GET /api/platform/subscriptions/{id}` | Detalle |
| **Platform Dashboard** | `GET /api/platform/dashboard/summary` | Dashboard del superadmin |

---

## 7. Estructura esperada para el módulo Platform

Siguiendo la arquitectura FSD, el nuevo módulo debe ubicarse en:

```
src/app/
├── features/
│   └── platform/
│       ├── application/
│       │   ├── platform-login.usecase.ts
│       │   ├── plan-list.usecase.ts
│       │   ├── plan-create.usecase.ts
│       │   ├── tenant-list.usecase.ts
│       │   ├── tenant-create.usecase.ts
│       │   ├── subscription-assign.usecase.ts
│       │   └── platform-dashboard-summary.usecase.ts
│       ├── ui/
│       │   ├── platform-login-form/
│       │   ├── plan-table/
│       │   ├── plan-form/
│       │   ├── tenant-table/
│       │   ├── tenant-form/
│       │   └── platform-dashboard-cards/
│       └── lib/
│           ├── platform.facade.ts
│           └── platform-storage.service.ts
├── pages/
│   └── platform-login-page/
│   └── platform-dashboard-page/
│   └── platform-plans-page/
│   └── platform-tenants-page/
│   └── platform-subscriptions-page/
└── shared/
    └── api/
        └── guards/
            ├── platform-auth.guard.ts
            └── platform-permission.guard.ts
```

---

## 8. Consideraciones técnicas para el asistente

### 8.1 Patrones a respetar

- **Casos de uso**: Cada acción de negocio debe ser un `*.usecase.ts`
- **Facade**: Exponer funcionalidades complejas a través de `*.facade.ts`
- **Servicios de API**: Solo comunicarse con backend, sin lógica de negocio
- **Componentes**: Solo presentación, llamar a usecases/facades

### 8.2 Autenticación

- **Platform**: Token en `platformAccessToken` (sin tenant slug)
- **Tenant**: Token en `tenantAccessToken` + header `X-Tenant-Slug`

### 8.3 Headers

```typescript
// Para platform
headers: { Authorization: `Bearer ${platformAccessToken}` }

// Para tenant
headers: { 
  Authorization: `Bearer ${tenantAccessToken}`,
  'X-Tenant-Slug': tenantSlug 
}
```

### 8.4 Flujo de navegación esperado

```
/login (tenant)          → dashboard tenant (actual)
/platform/login          → dashboard platform (nuevo)
/signup (público)        → creación de tenant (actual)
```

---

## 9. Resumen para el asistente

> **Tu misión principal es implementar el módulo PLATFORM (SuperAdmin) que actualmente NO EXISTE en el frontend.**
>
> El backend ya tiene todos los endpoints listos. Debes crear las features, páginas y componentes necesarios siguiendo la misma arquitectura FSD que ya está implementada para el módulo tenant.
>
> El `OWNER_ADMIN` es el dueño del tenant (ya implementado). El `SUPERADMIN` es el dueño de la plataforma (NUEVO, por implementar).
>
> Usa el mismo patrón que ya existe en `features/auth`, `features/user-management`, etc., pero adaptado para platform.
>
> **Prioridad**: Implementar login de platform, dashboard de platform, y CRUD de planes y tenants.

--- 

# 10. Guía completa Postman

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

---

# 13. 📁 Archivos que la IA debe leer para entender el proyecto

Para que la IA comprenda la arquitectura, estilos y patrones del proyecto, debe analizar los siguientes archivos:

## 13.1 Arquitectura y patrones

### `src/app/entities/auth/api/auth.service.ts`

Contiene el servicio de autenticación de tenant. Muestra cómo se estructuran las llamadas API.

### `src/app/shared/api/interceptors/auth.interceptor.ts`

Interceptor que agrega automáticamente el token `Authorization` y el header `X-Tenant-Slug` a las peticiones.

### `src/app/features/auth/application/login.usecase.ts`

Ejemplo de un caso de uso completo con manejo de estado (loading, error, success) y almacenamiento de tokens.

```typescript
// Ejemplo de estructura de usecase
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { LoginRequest } from '../../../entities/auth/model/login-request.model';
import { AuthService } from '../../../entities/auth/api/auth.service';
import { AuthStorageService } from '../../../shared/lib/storage/auth-storage.service';

export interface LoginState {
  status: 'idle' | 'loading' | 'success' | 'error';
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class LoginUseCase {
  private readonly authService = inject(AuthService);
  private readonly authStorage = inject(AuthStorageService);

  private readonly state = signal<LoginState>({ status: 'idle', error: null });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);

  async login(request: LoginRequest): Promise<void> {
    this.state.set({ status: 'loading', error: null });

    try {
      const response = await firstValueFrom(this.authService.login(request));

      if (response.success && response.data) {
        this.authStorage.saveToken(response.data.accessToken);
        if (response.data.refreshToken) {
          this.authStorage.saveRefreshToken(response.data.refreshToken);
        }
        if (request.tenantSlug) {
          this.authStorage.saveTenantSlug(request.tenantSlug);
        }
        this.state.set({ status: 'success', error: null });
      } else {
        this.state.set({ status: 'error', error: response.message || 'Error desconocido' });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', error: null });
  }
}
```

### `src/app/shared/lib/auth/auth.facade.ts`

Facade que expone funcionalidades complejas de autenticación a los componentes.

---

## 13.2 Estilos y UI

### `src/app/widgets/layoutAdmin/header/header.component.ts`

Header con menú de usuario, notificaciones y logout. Usa Tailwind CSS y animaciones.

### `src/app/widgets/layoutAdmin/sidebar/sidebar.component.ts`

Sidebar lateral con:
- Acordeones para agrupar menús
- Responsive (móvil colapsable)
- Íconos de Lucide
- Estilos consistentes con la paleta de colores

### `src/styles.css` o `src/app/app.css`

Contiene la paleta de colores global:

```css
:root {
  --primary-dark: #2E7D32;    /* verde oscuro (títulos, botones principales) */
  --primary-medium: #4CAF50;  /* verde medio (íconos, acentos) */
  --primary-light: #C8E6C9;   /* verde claro (fondos de estado) */
  --success-bg: #E8F5E9;      /* fondo verde claro para badges */
  --error-bg: #FFEBEE;        /* fondo rojo claro para badges */
  --text-primary: #333333;
  --text-secondary: #666666;
  --text-muted: #999999;
  --border-light: #C8E6C9;
  --bg-light: #F1F8E9;
}
```

---

## 13.3 Componentes de ejemplo

### `src/app/features/user-management/ui/user-table/user-table.component.html`

Tabla responsiva de usuarios (modelo para tablas de platform).

### `src/app/pages/dashboard-page/dashboard-page.component.html`

Layout de página con cards y estructura responsiva.

---

# 14. 🚨 Lo que la IA debe implementar

## 14.1 Módulo Platform (SuperAdmin) — NO EXISTE ACTUALMENTE

El frontend actual NO TIENE implementado el módulo de administración de la plataforma. El backend ya tiene todos los endpoints listos.

### Tareas pendientes:

| Módulo | Endpoints | UI necesaria |
|--------|-----------|--------------|
| **Platform Auth** | `POST /api/platform/auth/login` | Página de login de superadmin (sin campo tenant slug) |
| | `GET /api/platform/auth/me` | Header/drawer con info del superadmin |
| | `POST /api/platform/auth/change-password` | Modal de cambio de contraseña |
| **Platform Dashboard** | `GET /api/platform/dashboard/summary` | Dashboard con cards de resumen global |
| **Platform Plans** | `GET /api/platform/plans` | Tabla de planes |
| | `POST /api/platform/plans` | Formulario de creación |
| | `PATCH /api/platform/plans/{id}/activate` | Botón activar |
| | `PATCH /api/platform/plans/{id}/deactivate` | Botón desactivar |
| **Platform Tenants** | `GET /api/platform/tenants` | Tabla de tenants |
| | `POST /api/platform/tenants` | Formulario de creación manual |
| | `PATCH /api/platform/tenants/{id}/activate` | Botón activar |
| | `PATCH /api/platform/tenants/{id}/deactivate` | Botón desactivar |
| **Platform Subscriptions** | `POST /api/platform/subscriptions/assign` | Asignar plan a tenant |
| | `GET /api/platform/subscriptions` | Lista de suscripciones |

---

## 14.2 Estructura esperada (siguiendo FSD)

```
src/app/
├── features/
│   └── platform/
│       ├── application/
│       │   ├── platform-login.usecase.ts
│       │   ├── plan-list.usecase.ts
│       │   ├── plan-create.usecase.ts
│       │   ├── plan-toggle-status.usecase.ts
│       │   ├── tenant-list.usecase.ts
│       │   ├── tenant-create.usecase.ts
│       │   ├── tenant-toggle-status.usecase.ts
│       │   ├── subscription-assign.usecase.ts
│       │   └── platform-dashboard-summary.usecase.ts
│       ├── ui/
│       │   ├── platform-login-form/
│       │   ├── platform-dashboard-cards/
│       │   ├── plan-table/
│       │   ├── plan-form/
│       │   ├── tenant-table/
│       │   ├── tenant-form/
│       │   └── subscription-assign-form/
│       └── lib/
│           ├── platform.facade.ts
│           └── platform-storage.service.ts
├── pages/
│   ├── platform-login-page/
│   ├── platform-dashboard-page/
│   ├── platform-plans-page/
│   ├── platform-tenants-page/
│   └── platform-subscriptions-page/
└── shared/
    └── api/
        └── guards/
            ├── platform-auth.guard.ts
            └── platform-permission.guard.ts
```

---

## 14.3 Reglas importantes para la IA

1. **Platform NO usa `X-Tenant-Slug`** → solo `Authorization: Bearer {{platformAccessToken}}`
2. **Endpoints platform** → siempre empiezan con `/api/platform/`
3. **Guard** → crear `platform-auth.guard.ts` para rutas protegidas
4. **Diferencia clave**:
   - **Tenant** → `X-Tenant-Slug` + `Authorization`
   - **Platform** → solo `Authorization`
5. **Estilos**: usar Tailwind CSS, seguir paleta de colores existente
6. **Responsive**: mobile-first, breakpoint en 768px
7. **Componentes**: usar standalone components (sin NgModule)
8. **HTML**: usar template inline (string) en lugar de archivo separado
9. **Animaciones**: usar las mismas que header/sidebar (dropdownAnimation, fadeOverlay, accordion)

---

## 14.4 Ejemplo de página esperada (login de platform)

```typescript
// pages/platform-login-page/platform-login-page.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { PlatformLoginUseCase } from '../../features/platform/application/platform-login.usecase';

@Component({
  selector: 'app-platform-login-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  template: `
    <div class="min-h-screen bg-white flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div class="sm:mx-auto sm:w-full sm:max-w-md">
        <div class="text-center">
          <h2 class="mt-6 text-3xl font-extrabold text-[#2E7D32]">Plataforma SuperAdmin</h2>
          <p class="mt-2 text-sm text-[#666666]">Acceso exclusivo para administración global</p>
        </div>

        <div class="mt-8 bg-white py-8 px-4 shadow-xl rounded-lg sm:px-10 border border-[#C8E6C9]">
          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="space-y-6">
            <div>
              <label class="block text-sm font-medium text-[#2E7D32]">Correo electrónico</label>
              <input type="email" formControlName="email" class="mt-1 block w-full rounded-lg border-[#C8E6C9] px-3 py-2">
            </div>
            <div>
              <label class="block text-sm font-medium text-[#2E7D32]">Contraseña</label>
              <input type="password" formControlName="password" class="mt-1 block w-full rounded-lg border-[#C8E6C9] px-3 py-2">
            </div>
            <button type="submit" [disabled]="isLoading()" class="w-full bg-[#2E7D32] text-white py-2 rounded-lg">
              {{ isLoading() ? 'Cargando...' : 'Iniciar sesión' }}
            </button>
          </form>
        </div>
      </div>
    </div>
  `
})
export class PlatformLoginPageComponent {
  private fb = inject(FormBuilder);
  private loginUseCase = inject(PlatformLoginUseCase);
  private router = inject(Router);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  isLoading = this.loginUseCase.status;

  async onSubmit() {
    if (this.loginForm.invalid) return;
    await this.loginUseCase.login(this.loginForm.value);
    if (this.loginUseCase.status() === 'success') {
      this.router.navigate(['/platform/dashboard']);
    }
  }
}
```

---

## 14.5 Extender el sidebar con rutas de platform

```typescript
// Agregar en SidebarComponent
generalItems: MenuItem[] = [
  { label: 'Dashboard Platform', route: '/platform/dashboard', icon: 'layout-dashboard' },
  { label: 'Planes', route: '/platform/plans', icon: 'credit-card' },
  { label: 'Tenants', route: '/platform/tenants', icon: 'building-2' },
  { label: 'Suscripciones', route: '/platform/subscriptions', icon: 'receipt' }
];
```

---

# 15. Resumen para la IA

> **Tu misión principal es implementar el módulo PLATFORM (SuperAdmin) que actualmente NO EXISTE en el frontend.**
>
> El backend ya tiene todos los endpoints listos. Debes crear las features, páginas y componentes necesarios siguiendo la misma arquitectura FSD que ya está implementada para el módulo tenant.
>
> El `OWNER_ADMIN` es el dueño del tenant (ya implementado). El `SUPERADMIN` es el dueño de la plataforma (NUEVO, por implementar).
>
> Usa el mismo patrón que ya existe en `features/auth`, `features/user-management`, etc., pero adaptado para platform.
>
> **Prioridad**: Implementar login de platform, dashboard de platform, y CRUD de planes y tenants.
>
> **Estilos**: Usa Tailwind CSS, sigue la paleta de colores verde (#2E7D32, #4CAF50, #C8E6C9), respeta el diseño responsivo (mobile-first, breakpoint 768px).
>
> **Componentes**: Usa standalone components, template inline, animaciones existentes.