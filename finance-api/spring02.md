# Sistema Financiero Multitenant Sprint 2

Arquitectura modular inicial para una plataforma financiera SaaS multitenant.

---

## domain, (infr/persistence), (application/ dto,mapper, usecase)

# Visión General

## Módulos principales

```text
tenant/
├── identity
├── accounts
├── transactions
├── accounting
├── limits
├── fees
├── reporting
└── governance
```

---

# Capacidades que seran cubiertas

Con los módulos iniciales se pueden soportar:

- Wallet digital
- Cuenta de ahorro
- Cuenta corriente
- Transferencias
- Depósitos
- Retiros
- Pagos
- Reversos
- Comisiones
- Tarjetas
- Créditos
- Movimientos internos

---

# Módulos futuros

```text
limits         → límites diarios/mensuales
fees           → comisiones financieras
kyc            → verificación de identidad
aml            → prevención de lavado
risk           → antifraude
settlements    → conciliación/liquidación
beneficiaries  → destinatarios frecuentes
```

---

# Arquitectura Completa

```text
tenant/
├── accounts
├── transactions
├── accounting
├── limits
├── fees
├── kyc
├── aml
├── risk
├── settlements
├── reporting
└── governance
```

---


# 1. Accounts

Módulo encargado de administrar las cuentas financieras del sistema.

Representa dónde vive el dinero del cliente dentro de la plataforma financiera.

Este módulo actúa como núcleo financiero base para futuras operaciones:

- transferencias
- pagos
- depósitos
- retiros
- tarjetas
- préstamos
- límites
- liquidaciones
- contabilidad
- riesgo financiero

El módulo fue diseñado para ser reutilizable y escalable en entornos multitenant.

---

# Arquitectura

```text
accounts/
├── application
│   ├── dto
│   ├── mapper
│   ├── service
│   └── usecase
│
├── domain
│   ├── exception
│   ├── model
│   └── repository
│
└── infrastructure
    ├── api
    └── persistence
```

---

# Responsabilidades

- Crear cuentas financieras
- Administrar estados de cuenta
- Gestionar balances
- Relacionar cuentas con usuarios
- Administrar cuentas principales
- Administrar alias personalizados
- Gestionar cuentas multimoneda
- Generar números de cuenta
- Bloquear cuentas
- Congelar cuentas
- Suspender cuentas
- Cerrar cuentas
- Aprobar cuentas pendientes
- Obtener balances y metadata financiera
- Exponer información optimizada para frontend
- Registrar auditoría financiera

---

# Conceptos Importantes

## Account

Representa una cuenta financiera del sistema.

Ejemplos:

```text
Wallet principal
Cuenta de ahorro
Cuenta corriente
Tarjeta prepago
Cuenta de préstamo
```

---

## Account Number

Identificador único de la cuenta.

Formato:

```text
PREFIX-CURRENCY-YEAR-SEQUENCE
```

Ejemplo:

```text
WAL-USD-2026-000001
```

---

## Account Name

Nombre controlado por el sistema.

No permite strings libres.

Esto garantiza consistencia financiera y evita nombres inválidos.

---

## Custom Alias

Nombre personalizado opcional definido por el cliente.

Ejemplo:

```text
"Cuenta diaria"
"Cuenta para gastos"
"Ahorros viaje"
```

No reemplaza el tipo real de cuenta.

Solo es una capa visual para frontend.

---

## Display Name

Nombre final mostrado al usuario.

Prioridad:

```text
customAlias
↓
accountNameLabel
```

Ejemplo:

```text
"Cuenta diaria"
```

o si no existe alias:

```text
"Cuenta de ahorro"
```

---

# Tipos de Cuenta

```text
WALLET
SAVINGS
CHECKING
CREDIT_CARD
PREPAID_CARD
LOAN
```

---

# Nombres de Cuenta

```text
MAIN_WALLET
SAVINGS_ACCOUNT
CHECKING_ACCOUNT
CREDIT_CARD_ACCOUNT
PREPAID_CARD_ACCOUNT
LOAN_ACCOUNT
BUSINESS_ACCOUNT
SECONDARY_ACCOUNT
```

---

# Estados de Cuenta

```text
ACTIVE
BLOCKED
SUSPENDED
FROZEN
CLOSED
PENDING_VERIFICATION
PENDING_APPROVAL
```

---

# Significado de Estados

| Estado | Descripción |
|---|---|
| ACTIVE | Cuenta operativa |
| BLOCKED | Cuenta bloqueada administrativamente |
| SUSPENDED | Cuenta suspendida temporalmente |
| FROZEN | Fondos congelados preventivamente |
| CLOSED | Cuenta cerrada permanentemente |
| PENDING_VERIFICATION | Requiere validación/KYC |
| PENDING_APPROVAL | Requiere aprobación manual |

---

# Monedas Soportadas

```text
BOB
USD
EUR
USDT
```

---

# Reglas del Sistema

## Cuenta Principal

Un usuario puede tener múltiples cuentas.

Pero normalmente una sola cuenta principal:

```text
isPrimary = true
```

---

## Creación Automática vs Aprobación

El cliente puede crear directamente hasta:

```text
2 cuentas
```

A partir de la tercera:

```text
status = PENDING_APPROVAL
```

y requiere validación administrativa.

---

## Balances

La cuenta maneja:

```text
availableBalance
heldBalance
```

---

## Available Balance

Saldo disponible para operar.

---

## Held Balance

Saldo retenido temporalmente.

Ejemplos:

- retenciones
- fraude
- validaciones
- pagos pendientes
- disputas

---

## Total Balance

Calculado:

```text
availableBalance + heldBalance
```

---

# Endpoints Administrativos

```http
GET    /api/accounts
GET    /api/accounts/{id}

POST   /api/accounts

PUT    /api/accounts/{id}

PATCH  /api/accounts/{id}/approve
PATCH  /api/accounts/{id}/activate

PATCH  /api/accounts/{id}/block
PATCH  /api/accounts/{id}/freeze
PATCH  /api/accounts/{id}/close

GET    /api/accounts/{id}/balance
```

---

# Endpoints del Cliente Actual

```http
GET    /api/me/accounts
GET    /api/me/accounts/{id}

POST   /api/me/accounts

PATCH  /api/me/accounts/{id}/alias

GET    /api/me/accounts/{id}/balance
```

---

# Responses

Los endpoints NO devuelven solamente IDs.

Las respuestas están enriquecidas para frontend.

Ejemplo:

```json
{
  "id": "...",
  "accountNumber": "WAL-USD-2026-000001",

  "accountName": "MAIN_WALLET",
  "accountNameLabel": "Wallet principal",

  "customAlias": "Cuenta diaria",
  "displayName": "Cuenta diaria",

  "accountType": "WALLET",
  "currency": "USD",

  "availableBalance": 1000,
  "heldBalance": 100,
  "totalBalance": 1100,

  "status": "ACTIVE",

  "userId": "...",
  "userFullName": "Juan Perez",
  "userEmail": "juan@empresa.com"
}
```

---

# Auditoría

Todas las operaciones importantes generan auditoría:

```text
ACCOUNT_CREATED
ACCOUNT_UPDATED
ACCOUNT_BLOCKED
ACCOUNT_FROZEN
ACCOUNT_CLOSED
ACCOUNT_ALIAS_UPDATED
ACCOUNT_APPROVAL_REQUESTED
```

---

# Consideraciones Técnicas

- Arquitectura hexagonal
- Compatible con multitenancy
- Compatible con múltiples monedas
- Compatible con futuras tarjetas
- Compatible con préstamos
- Compatible con límites financieros
- Compatible con ledger/accounting
- Compatible con motores antifraude
- Compatible con auditoría financiera
- Compatible con KYC/AML

---

# Consideraciones Financieras

El balance NO debe considerarse la única fuente absoluta de verdad.

En sistemas financieros reales:

```text
ledger/accounting
```

debe ser la fuente financiera principal.

La cuenta funciona como:

```text
estado financiero operacional
```

optimizado para consultas rápidas.

---

# Futuras Integraciones

El módulo fue diseñado para integrarse con:

```text
transactions
payments
cards
limits
fees
risk
settlements
ledger
accounting
loans
investments
crypto-assets
```


# 2. Transactions

Módulo encargado de procesar operaciones financieras.

---

## Estructura

```text
transactions/
├── transfer
├── deposit
├── withdrawal
├── payment
├── reversal
├── transaction-history
├── transaction-status
└── transaction-validation
```

---

## Responsabilidades

- Transferencias
- Depósitos
- Retiros
- Pagos
- Reversos
- Historial transaccional
- Validaciones de operación
- Integración con limits
- Integración con fees
- Integración con accounting

---

## Tipos de Transacción

```text
TRANSFER
DEPOSIT
WITHDRAWAL
PAYMENT
REVERSAL
REFUND
FEE
ADJUSTMENT
```

---

## Estados de Transacción

```text
PENDING
PROCESSING
AUTHORIZED
COMPLETED
FAILED
REVERSED
CANCELLED
EXPIRED
```

---

## Endpoints

```http
POST   /api/transactions/transfers
POST   /api/transactions/deposits
POST   /api/transactions/withdrawals
POST   /api/transactions/payments

POST   /api/transactions/{id}/reverse

GET    /api/transactions
GET    /api/transactions/{id}

GET    /api/accounts/{accountId}/transactions
```

---

## Campos Importantes

```text
idempotency_key
external_reference
failure_reason
processed_at
reversed_transaction_id
```

---

## Consideraciones Técnicas

- Todas las operaciones deben ser transaccionales
- Debe existir idempotencia
- Debe soportar reintentos seguros
- Debe integrarse con auditoría
- Debe soportar reversos seguros
- Debe manejar concurrencia

---

# 3. Accounting

Módulo encargado de la contabilidad formal del tenant.

Este módulo NO representa las cuentas del cliente.  
Representa la contabilidad interna del negocio.

---

## Estructura

```text
accounting/
├── chart-of-accounts
├── journal-entries
├── journal-lines
├── accounting-periods
├── accounting-rules
└── accounting-reports
```

---

## Responsabilidades

- Libro diario
- Asientos contables
- Plan de cuentas
- Débitos y créditos
- Balance contable
- Estados financieros
- Integración con transactions

---

## Tipos de Cuenta Contable

```text
ASSET
LIABILITY
EQUITY
INCOME
EXPENSE
```

---

## Endpoints

```http
GET    /api/accounting/accounts
POST   /api/accounting/accounts

PATCH  /api/accounting/accounts/{id}

GET    /api/accounting/journal-entries
GET    /api/accounting/journal-entries/{id}

POST   /api/accounting/journal-entries

GET    /api/accounting/periods
POST   /api/accounting/periods

PATCH  /api/accounting/periods/{id}/close
```

---

## Consideraciones Técnicas

- Toda transacción debe generar asientos contables
- Debe existir partida doble
- Los periodos deben poder cerrarse
- No debe permitirse modificar periodos cerrados
- Debe soportar auditoría financiera

---

# 4. Limits

Módulo encargado de validar límites operativos y financieros.

---

## Estructura

```text
limits/
├── limit-rules
├── limit-usage
├── limit-evaluation
└── limit-overrides
```

---

## Responsabilidades

- Limitar montos diarios
- Limitar montos mensuales
- Limitar cantidad de operaciones
- Limitar por usuario
- Limitar por cuenta
- Limitar por rol
- Validar operaciones antes de procesarlas

---

## Ejemplos de Límites

- Máximo retiro diario
- Máximo depósito mensual
- Máximo pago por operación
- Máximo transferido por día
- Cantidad máxima de transacciones

---

## Endpoints

```http
GET    /api/limits/rules
POST   /api/limits/rules

PATCH  /api/limits/rules/{id}
DELETE /api/limits/rules/{id}

POST   /api/limits/evaluate

GET    /api/limits/usage
GET    /api/accounts/{accountId}/limits
```

---

## Consideraciones Técnicas

- Debe ejecutarse antes de procesar operaciones
- Debe soportar múltiples monedas
- Debe soportar límites dinámicos
- Debe soportar excepciones por rol

---

# 5. Fees

Módulo encargado de gestionar comisiones y cargos financieros.

---

## Estructura

```text
fees/
├── fee-rules
├── fee-calculation
├── fee-charges
└── fee-settlement
```

---

## Responsabilidades

- Definir reglas de comisión
- Calcular cargos
- Aplicar fees automáticos
- Integrarse con accounting
- Integrarse con transactions

---

## Tipos de Fees

```text
FIXED
PERCENTAGE
MIXED
TIERED
```

---

## Endpoints

```http
GET    /api/fees/rules
POST   /api/fees/rules

PATCH  /api/fees/rules/{id}
DELETE /api/fees/rules/{id}

POST   /api/fees/calculate

GET    /api/fees/charges
GET    /api/transactions/{id}/fees
```

---

## Consideraciones Técnicas

- Debe soportar comisiones dinámicas
- Debe soportar impuestos
- Debe generar movimientos contables
- Debe permitir promociones futuras

---

# 6. Reporting

Módulo encargado de reportes y visualización financiera.

---

## Estructura

```text
reporting/
├── reports
├── report-exports
├── dashboards
├── analytics
└── report-history
```

---

## Responsabilidades

- Reportes financieros
- Reportes operativos
- Exportaciones
- Dashboard ejecutivo
- KPIs financieros
- Consultas históricas

---

## Tipos de Reporte

```text
FINANCIAL
TRANSACTIONAL
USERS
ACCOUNTING
CUSTOM
```

---

## Endpoints

```http
GET    /api/reports
GET    /api/reports/{id}

POST   /api/reports/{id}/run
POST   /api/reports/{id}/export

GET    /api/reports/exports
GET    /api/reports/exports/{id}

GET    /api/dashboard/financial-summary
GET    /api/dashboard/transactions-summary
GET    /api/dashboard/accounts-summary
```

---

## Consideraciones Técnicas

- Las exportaciones deben ser auditadas
- Debe soportar PDF, CSV y XLSX
- Debe soportar filtros avanzados
- Debe evitar SQL dinámico inseguro

---

# 7. Governance

Módulo encargado de auditoría, trazabilidad y eventos del sistema.

---

## Estructura

```text
governance/
├── audit-events
├── notifications
├── system-events
├── activity-history
└── security-events
```

---

## Responsabilidades

- Auditoría de acciones
- Registro de eventos críticos
- Notificaciones
- Seguridad operacional
- Trazabilidad financiera

---

## Tipos de Eventos

```text
LOGIN
LOGIN_FAILED
TRANSFER
PAYMENT
ROLE_ASSIGNMENT
EXPORT
ACCOUNT_BLOCKED
PASSWORD_CHANGE
```

---

## Endpoints

```http
GET    /api/audit-events
GET    /api/audit-events/{id}

GET    /api/notifications

PATCH  /api/notifications/{id}/read
PATCH  /api/notifications/read-all

GET    /api/system-events
```

---

## Consideraciones Técnicas

- Toda operación crítica debe auditarse
- La auditoría no debe poder modificarse
- Debe soportar metadata JSON
- Debe registrar IP y user-agent
- Debe permitir trazabilidad completa

---

# Relación entre Módulos

## Accounts

Representa dónde vive el dinero.

---

## Transactions

Representa cómo se mueve el dinero.

---

## Accounting

Representa cómo se registra contablemente.

---

## Limits

Valida restricciones operativas.

---

## Fees

Aplica comisiones y cargos.

---

## Reporting

Visualiza información financiera.

---

## Governance

Garantiza auditoría y trazabilidad.


