# Sistema Financiero Multitenant Sprint 2

Arquitectura modular para una plataforma financiera SaaS multitenant.
Este documento describe el estado actual real del sistema y deja una ruta
clara para los modulos pendientes.

---

## Stack y convenciones

```text
domain                 -> model, repository, exception
application            -> dto, mapper, service, usecase
infrastructure         -> api, persistence, adapter
```

## Cómo leer esta documentación

Si vas a implementar frontend o integraciones, este es el orden útil de lectura:

1. `Accounts` para entender dónde vive el dinero.
2. `Transactions` para entender cómo se mueve el dinero.
3. `FX y Comisiones` para entender conversiones y cargos.
4. `Limits` para entender bloqueos y revisiones.
5. `Accounting` para entender el registro formal.
6. `Governance` para entender auditoría y trazabilidad.
7. `Notifications` para entender el inbox, push y alertas operativas.
8. `Reporting` para entender analítica futura.
9. `Cross-Tenant` para entender la siguiente fase de producto.

La idea es que `Accounts` y `Transactions` permitan operar el banco base desde el inicio, mientras el resto de módulos crece encima de ese núcleo.

---

# Visión General

## Mapa actual

```text
tenant/
├── identity
├── accounts
├── transactions
├── fx
├── limits
├── accounting
├── governance
├── notifications
└── reporting
```

---

# Capacidades actuales

Hoy la plataforma soporta:

- Cuentas financieras multimoneda
- Depositos
- Retiros
- Transferencias internas dentro del tenant
- Pagos
- Holds y releases
- Fees y adjustments operativos
- Reversos y refunds completos
- Intentos QR y confirmacion
- Conversion de moneda con tasa y comision
- Limites configurables por tenant
- Contabilidad formal por asiento y periodos
- Auditoria y trazabilidad
- Notificaciones in-app y push derivadas del core

## Base operativa de banco

Con la base actual ya se puede operar un banco tenant de forma funcional para el flujo principal:

- crear usuarios cliente
- crear cuentas con tipo y moneda
- asignar permisos por defecto
- depositar dinero incluso con conversion de moneda
- transferir entre cuentas del mismo tenant
- cobrar retiros y pagos
- aplicar hold y release
- aplicar ajustes y fees
- revertir y reembolsar operaciones completas
- registrar asientos contables balanceados
- aplicar limites por tenant, usuario, cuenta o tipo de transaccion
- administrar tasas de cambio y comisiones desde `OWNER_ADMIN`

Esto es suficiente para que el frontend trabaje con una base real de banca operativa sin necesitar los modulos futuros completos.

---

# Seeders por defecto

La creacion de un tenant nuevo deja configurado el sistema base sin trabajo manual:

- Roles tenant: `OWNER_ADMIN`, `ADMIN`, `USER`
- Permisos base de cuentas, transacciones, limits, accounting y fx
- Permisos `me.*` para usuarios cliente
- Tasas de cambio por defecto entre `BOB`, `USD`, `EUR` y `USDT`
- Comisiones por operacion en `0` por defecto
- Configuracion basica del tenant

## Resultado esperado en un tenant nuevo

Un tenant nuevo ya puede:

- crear clientes
- crear cuentas
- hacer depositos
- hacer transferencias internas
- consultar balances
- ver transacciones con detalle de moneda y conversion

---

# Contrato de errores

La API devuelve un sobre de error consistente para que frontend pueda mostrar mensajes claros sin adivinar el origen del fallo.

## Forma general de la respuesta

```json
{
  "success": false,
  "message": "Human readable error message",
  "errors": [
    "Optional technical detail or field-specific reason"
  ],
  "timestamp": "2026-05-16T21:49:45.535Z"
}
```

## Códigos HTTP más comunes

| Código | Uso |
|---|---|
| `400` | Validación de negocio, cuerpo inválido, moneda invalida, saldo insuficiente, limite excedido |
| `401` | No autenticado o token invalido |
| `403` | Sin permiso suficiente o acceso denegado |
| `404` | Recurso no encontrado |
| `409` | Conflicto de datos, idempotencia reutilizada con payload distinto, violación de integridad |
| `422` | Puede usarse en validaciones de negocio mas especificas si el controlador lo expone |
| `500` | Error inesperado del servidor |

## Reglas para frontend

- usar `message` como texto principal
- usar `errors[]` para detalle tecnico o validaciones especificas
- no asumir que un `500` significa siempre error de negocio
- si el error es conocido, la API intenta devolver motivo concreto, no un genérico

---


# Arquitectura objetivo

```text
tenant/
├── identity
├── accounts
├── transactions
├── fx
├── limits
├── accounting
├── governance
└── reporting
```

---

# Módulos futuros

```text
kyc            -> verificacion de identidad
aml            -> prevencion de lavado
risk           -> antifraude
beneficiaries   -> destinatarios frecuentes
cards          -> tarjetas
loans          -> prestamos
investments    -> inversiones
statements     -> extractos
cross-tenant   -> transferencias entre tenants
settlements    -> compensacion futura entre tenants
```


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

## Request Bodies

Estos son los campos que aceptan los endpoints que crean o actualizan cuentas:

### `POST /api/accounts`

| Campo | Uso |
|---|---|
| `userId` | Usuario dueño de la cuenta |
| `accountName` | Tipo de cuenta controlado por enum |
| `customAlias` | Alias visual opcional para frontend |
| `accountType` | Tipo operativo de la cuenta |
| `currency` | Moneda base de la cuenta |
| `primary` | Marca si será cuenta principal |

### `POST /api/me/accounts`

| Campo | Uso |
|---|---|
| `accountName` | Tipo de cuenta controlado por enum |
| `customAlias` | Alias visual opcional |
| `accountType` | Tipo operativo de la cuenta |
| `currency` | Moneda base de la cuenta |

### `PUT /api/accounts/{id}`

| Campo | Uso |
|---|---|
| `accountName` | Reemplaza el nombre/tipo visible de la cuenta |
| `customAlias` | Actualiza el alias visual |
| `primary` | Cambia si es cuenta principal |

### `PATCH /api/accounts/{id}/status`

| Campo | Uso |
|---|---|
| `status` | Nuevo estado de la cuenta (`ACTIVE`, `BLOCKED`, `FROZEN`, etc.) |

### `PATCH /api/me/accounts/{id}/alias`

| Campo | Uso |
|---|---|
| `customAlias` | Nuevo alias visual para la cuenta |

## Como leer estos cuerpos

- `accountName` no es texto libre, es un enum de negocio.
- `currency` define la moneda fija de la cuenta.
- `primary` sirve para marcar la cuenta principal del usuario.
- `customAlias` es opcional y solo ayuda a frontend.
- `accountType` en la creacion de cuentas solo admite `WALLET`, `SAVINGS`, `CHECKING` o `PREPAID_CARD`.
- `CREDIT_CARD` y `LOAN` quedan reservadas para modulos futuros de credito y tarjetas.

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

Estos valores no son `AccountType`.
Son `AccountName`, es decir, etiquetas o variantes visuales de una cuenta.

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

Modulo encargado de orquestar las operaciones financieras del tenant.

Este modulo no es solo un CRUD de movimientos. Es el orquestador que:

- valida la solicitud
- aplica conversion de moneda si corresponde
- consulta limites
- genera movimientos
- actualiza saldos
- crea el asiento contable
- devuelve una respuesta util para frontend y auditoria

## Tipos de cuenta permitidos para transacciones

En el core actual solo estas cuentas participan en operaciones de dinero:

- `WALLET`
- `SAVINGS`
- `CHECKING`
- `PREPAID_CARD`

Estas cuentas pueden usar:

- depósitos
- retiros
- transferencias
- pagos
- holds y releases
- fees
- ajustes
- confirmación QR

Estos tipos quedan fuera del flujo operativo de transacciones genéricas en esta fase:

- `CREDIT_CARD`
- `LOAN`

Si el frontend intenta operar con esos tipos, el backend responde con un error claro indicando que la cuenta no es apta para transacciones genéricas y que debe usarse una cuenta transaccional como `WALLET`, `SAVINGS`, `CHECKING` o `PREPAID_CARD`.

## Matriz operativa por tipo de cuenta

### Cuentas transaccionales

| Tipo | Deposit | Transfer | Withdrawal | Payment | Hold/Release | QR |
|---|---|---|---|---|---|---|
| `WALLET` | Sí | Sí | Sí | Sí | Sí | Sí |
| `SAVINGS` | Sí | Sí | Sí | Sí | Sí | Sí |
| `CHECKING` | Sí | Sí | Sí | Sí | Sí | Sí |
| `PREPAID_CARD` | Sí | Sí | Sí | Sí | Sí | Sí |

### Cuentas de crédito o deuda

| Tipo | Deposit genérico | Transfer genérica | Withdrawal genérico | Payment genérico | Hold/Release | QR |
|---|---|---|---|---|---|---|
| `CREDIT_CARD` | No | No | No | No | No | No |
| `LOAN` | No | No | No | No | No | No |

Estas cuentas no desaparecen del sistema. Solo quedan fuera del core de transacciones genéricas porque requieren reglas propias:

- `CREDIT_CARD` luego puede usar compras, pagos, avances y reversos de tarjeta
- `LOAN` luego puede usar desembolsos, amortizaciones, intereses y cargos

## Request Bodies

### `POST /api/transactions/deposits`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `targetAccountId` | `UUID` | Cuenta que recibirá el dinero | Debe existir y pertenecer al tenant activo. |
| `amount` | `BigDecimal` | Monto del deposito | Debe ser mayor que `0`. |
| `currency` | `CurrencyCode` | Moneda enviada por el canal de entrada | Enum rígido: `BOB`, `USD`, `EUR`, `USDT`. |
| `method` | `TransactionChannel` | Canal operativo de captura | Opcional. Si no se envía, el backend usa `CASHBOX`. |
| `externalReference` | `String` | Referencia de negocio o comprobante externo | Útil para caja, POS o referencias internas. |
| `description` | `String` | Descripción legible para auditoría y frontend | Recomendado para UX y trazabilidad. |
| `idempotencyKey` | `String` | Clave única por intento para evitar duplicados | La genera el frontend. Reutilízala si reintentas el mismo depósito. |

Valores válidos de `method`:

- `CASHBOX`
- `API`
- `MANUAL`
- `ADMIN`
- `SYSTEM`

### `POST /api/transactions/transfers`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `sourceAccountId` | `UUID` | Cuenta que enviará el dinero | Debe pertenecer al tenant. |
| `targetAccountId` | `UUID` | Cuenta que recibirá el dinero | Puede ser de otra moneda; el backend convierte automáticamente. |
| `amount` | `BigDecimal` | Monto a transferir | Debe ser mayor que `0`. |
| `currency` | `CurrencyCode` | Moneda de salida de la transferencia | Debe coincidir con la moneda de la cuenta origen. |
| `description` | `String` | Descripción opcional de la transferencia | Recomendado para frontend y auditoría. |
| `idempotencyKey` | `String` | Clave única por intento | Reutilízala si el cliente reintenta el mismo envío. |

### `POST /api/transactions/withdrawals`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `sourceAccountId` | `UUID` | Cuenta desde la que se descontará el dinero | Debe existir y estar activa. |
| `amount` | `BigDecimal` | Monto del retiro | Debe ser mayor que `0`. |
| `currency` | `CurrencyCode` | Moneda del retiro | Debe coincidir con la moneda de la cuenta. |
| `method` | `TransactionChannel` | Canal operativo usado para retirar | Opcional. Si no se envía, el backend usa `CASHBOX`. |
| `description` | `String` | Descripción opcional | Útil para el frontend y auditoría. |
| `idempotencyKey` | `String` | Clave única por intento | Reutilízala al repetir el mismo retiro. |

Valores válidos de `method`:

- `CASHBOX`
- `API`
- `MANUAL`
- `ADMIN`

### `POST /api/transactions/payments`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `sourceAccountId` | `UUID` | Cuenta que paga | Debe existir y pertenecer al usuario autorizado. |
| `amount` | `BigDecimal` | Monto del pago | Debe ser mayor que `0`. |
| `currency` | `CurrencyCode` | Moneda del pago | Debe coincidir con la moneda de la cuenta fuente. |
| `method` | `TransactionChannel` | Canal operativo de pago | Opcional. Si no se envía, el backend usa `API`. |
| `externalReference` | `String` | Referencia externa o del comercio | Ideal para conciliación y QR. |
| `description` | `String` | Descripción legible | Recomendado para UX. |
| `idempotencyKey` | `String` | Clave única por intento | Reutilízala para evitar doble cobro en reintentos. |

Valores válidos de `method`:

- `QR`
- `API`
- `MANUAL`
- `ADMIN`
- `SYSTEM`

### `POST /api/transactions/holds`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `accountId` | `UUID` | Cuenta sobre la que se retiene saldo | Debe existir y estar activa. |
| `amount` | `BigDecimal` | Monto retenido | Debe ser mayor que `0`. |
| `currency` | `CurrencyCode` | Moneda de la cuenta | Debe coincidir con la moneda real de la cuenta. |
| `description` | `String` | Motivo de la retención | Recomendado para auditoría. |
| `idempotencyKey` | `String` | Clave única por intento | Evita duplicar el hold si el frontend reintenta. |

### `POST /api/transactions/releases`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `accountId` | `UUID` | Cuenta sobre la que se libera saldo | Debe existir y estar activa. |
| `amount` | `BigDecimal` | Monto liberado | Debe ser mayor que `0`. |
| `currency` | `CurrencyCode` | Moneda de la cuenta | Debe coincidir con la moneda real de la cuenta. |
| `description` | `String` | Motivo de la liberación | Recomendado para UX. |
| `idempotencyKey` | `String` | Clave única por intento | Reutilízala solo para el mismo release. |

### `POST /api/transactions/fees`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `accountId` | `UUID` | Cuenta afectada por la comisión | Debe existir y estar activa. |
| `amount` | `BigDecimal` | Monto cobrado | Debe ser mayor que `0`. |
| `currency` | `CurrencyCode` | Moneda del cargo | Debe coincidir con la moneda de la cuenta. |
| `externalReference` | `String` | Referencia del cobro | Útil para conciliación. |
| `description` | `String` | Motivo del cargo | Recomendado para auditoría. |
| `idempotencyKey` | `String` | Clave única por intento | Evita cargos duplicados por reintentos. |

### `POST /api/transactions/adjustments`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `accountId` | `UUID` | Cuenta ajustada | Debe existir y estar activa. |
| `amount` | `BigDecimal` | Monto del ajuste | Debe ser mayor que `0`. |
| `currency` | `CurrencyCode` | Moneda de la cuenta | Debe coincidir con la cuenta. |
| `direction` | `AdjustmentDirection` | Sentido del ajuste | Enum rígido: `CREDIT` aumenta, `DEBIT` disminuye. |
| `reason` | `String` | Razón obligatoria del ajuste | Debe ser clara y auditable. |
| `externalReference` | `String` | Referencia externa o interna | Opcional. |
| `idempotencyKey` | `String` | Clave única por intento | Reutilízala solo para el mismo ajuste. |

Valores válidos de `direction`:

- `CREDIT`
- `DEBIT`

### `POST /api/transactions/qr/intents`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `targetAccountId` | `UUID` | Cuenta que recibirá el pago/cobro QR | Debe existir y estar activa. |
| `amount` | `BigDecimal` | Monto fijo del intent QR | El QR actual es de monto fijo, no abierto. |
| `currency` | `CurrencyCode` | Moneda del intent | Debe coincidir con la moneda de la cuenta destino. |
| `externalReference` | `String` | Referencia de negocio | Útil para comercio o comprobante. |
| `description` | `String` | Descripción visible para el usuario | El frontend la puede mostrar antes de confirmar. |
| `idempotencyKey` | `String` | Clave única del intent QR | Reutilízala si el usuario reintenta crear el mismo intent. |

### `POST /api/transactions/{id}/reversal`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `reason` | `String` | Motivo obligatorio del reversal | Debe explicar por qué se deshace la operación. |
| `idempotencyKey` | `String` | Clave única de la reversa | Evita duplicar el reversal. |

### `POST /api/transactions/{id}/refunds`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `amount` | `BigDecimal` | Debe coincidir con el monto total original | El reembolso es completo, no parcial. |
| `reason` | `String` | Motivo del reembolso | Recomendado para trazabilidad. |
| `idempotencyKey` | `String` | Clave única del reembolso | No permite duplicar el mismo reembolso. |

### `POST /api/me/transactions/qr/{id}/confirm`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `sourceAccountId` | `UUID` | Cuenta del usuario que pagará el QR | Debe pertenecer al usuario autenticado. |
| `idempotencyKey` | `String` | Clave única por confirmación | El frontend la reutiliza si reintenta confirmar el mismo QR. |

## Como entender `idempotencyKey`

`idempotencyKey` no es un dato del banco. Lo genera el frontend o el cliente que llama a la API.

Sirve para que el backend reconozca un intento repetido y no cobre dos veces.

Regla práctica:

- generar un valor único por clic/envío del formulario
- no reutilizarlo en otra operación distinta
- conservarlo si el cliente reintenta por timeout o red
- idealmente generarlo con `crypto.randomUUID()` en el frontend
- guardarlo en memoria o `sessionStorage` mientras el formulario siga vivo

Ejemplo:

```text
deposit-2026-05-16-0001
transfer-7b8c2a3d-2a9e-4d5b-9c7a-0fd1a2cc8f90
qr-confirm-01
```

Si el usuario vuelve a enviar exactamente la misma intención, el backend usa esa clave para devolver la operación ya creada o bloquear duplicidad.
Si la operación ya fue confirmada, esa misma `idempotencyKey` no debe reutilizarse para una operación nueva.

## Qué significa cada campo menos obvio

- `method`: canal operativo. Úsalo para indicar si el dinero entró por caja, API, manual, etc.
- `externalReference`: referencia externa del negocio, comprobante o comercio.
- `description`: texto legible para auditoría y frontend.
- `direction`: sentido del ajuste, normalmente incremento o decremento.
- `reason`: motivo obligatorio para operaciones sensibles como reversal, refund o adjustments.
- `currency`: en el core actual debe tratarse como `CurrencyCode`.
- `method`: en el core actual debe tratarse como `TransactionChannel`.
- `direction`: en el core actual debe tratarse como `AdjustmentDirection`.

## Arquitectura

```text
transactions/
├── application
│   ├── dto
│   ├── mapper
│   ├── port
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

## Responsabilidades

- Transferencias internas entre cuentas del mismo tenant
- Depositos de dinero
- Retiros de dinero
- Pagos internos
- Holds y releases
- Fees y adjustments
- Reversos completos
- Refunds completos
- Intentos y confirmaciones QR
- Integracion con `limits`
- Integracion con `fx`
- Integracion con `accounting`
- Auditoria de eventos de negocio

## Flujos soportados hoy

Los flujos que ya puede entender frontend y negocio son:

1. **Deposito**
   - entra dinero a una cuenta
   - puede convertir moneda si la cuenta destino usa otra moneda

2. **Transferencia interna**
   - mueve dinero entre dos cuentas del mismo tenant
   - puede convertir moneda en el credito final

3. **Retiro**
   - saca dinero de una cuenta
   - valida saldo disponible y moneda de la cuenta origen

4. **Pago**
   - debita una cuenta para saldar una operacion interna
   - puede nacer desde QR

5. **Hold / Release**
   - reserva temporalmente un saldo
   - libera ese saldo luego

6. **Refund / Reversal**
   - corrige o deshace una operacion ya completada

## Lo que el frontend puede construir con esto

- formularios de depositos
- formularios de transferencias
- flujo de QR para cobros/pagos
- historial transaccional con detalle
- vista de conversion de moneda
- vista de comisiones
- vista de estados y errores
- acciones de reversal/refund para usuarios con permiso

## Tipos de transaccion

```text
TRANSFER
DEPOSIT
WITHDRAWAL
PAYMENT
REVERSAL
REFUND
FEE
ADJUSTMENT
HOLD
RELEASE
SETTLEMENT
```

### Significado de cada tipo

| Tipo | Uso |
|---|---|
| `TRANSFER` | Movimiento entre dos cuentas del mismo tenant |
| `DEPOSIT` | Ingreso de dinero a una cuenta |
| `WITHDRAWAL` | Salida de dinero desde una cuenta |
| `PAYMENT` | Cargo desde una cuenta hacia otra cuenta o destino interno |
| `REVERSAL` | Reversion total de una operacion anterior |
| `REFUND` | Devolucion total de un monto cobrado |
| `FEE` | Cargo por comision u operacion |
| `ADJUSTMENT` | Ajuste manual o administrativo |
| `HOLD` | Retencion temporal de saldo |
| `RELEASE` | Liberacion de una retencion |
| `SETTLEMENT` | Concepto de liquidacion/compensacion futura; no es endpoint publico del core actual |

## Estados de transaccion

```text
PENDING
PENDING_REVIEW
PROCESSING
AUTHORIZED
COMPLETED
FAILED
REVERSED
PARTIALLY_REFUNDED
CANCELLED
EXPIRED
```

### Significado de cada estado

| Estado | Uso |
|---|---|
| `PENDING` | La transaccion fue creada pero aun no se ejecuta |
| `PENDING_REVIEW` | Estado reservado para una futura cola de aprobacion; actualmente no forma parte del flujo operativo |
| `PROCESSING` | El backend la esta ejecutando |
| `AUTHORIZED` | La operacion fue autorizada |
| `COMPLETED` | Finalizada correctamente |
| `FAILED` | Fallo por saldo, regla o error tecnico |
| `REVERSED` | La operacion fue revertida |
| `PARTIALLY_REFUNDED` | Hubo devolucion parcial |
| `CANCELLED` | Fue cancelada antes de completarse |
| `EXPIRED` | Vencio por tiempo o vigencia del intent |

## Canales de transaccion

```text
MANUAL
QR
API
ADMIN
CASHBOX
SCHEDULED
EXTERNAL_BANK
SYSTEM
```

### Significado de cada canal

| Canal | Uso |
|---|---|
| `MANUAL` | Operacion iniciada manualmente |
| `QR` | Operacion iniciada o confirmada por QR |
| `API` | Operacion creada por integracion tecnica |
| `ADMIN` | Operacion ejecutada por un administrador |
| `CASHBOX` | Operacion de caja o ventanilla |
| `SCHEDULED` | Operacion programada |
| `EXTERNAL_BANK` | Canal reservado para integraciones externas futuras |
| `SYSTEM` | Operacion generada por automatizacion del sistema |

## Movimientos

```text
DEBIT
CREDIT
HOLD
RELEASE
```

### Significado de cada movimiento

| Movimiento | Uso |
|---|---|
| `DEBIT` | Reduce saldo o representa salida de fondos |
| `CREDIT` | Aumenta saldo o representa entrada de fondos |
| `HOLD` | Retiene saldo sin moverlo definitivamente |
| `RELEASE` | Devuelve saldo retenido al disponible |

## QrTransactionIntent

La entidad `QrTransactionIntent` separa la intencion de cobro/pago de la ejecucion real.

En la implementacion actual funciona como un intent de cobro por QR:

- el receptor crea el intento
- el intent queda listo para ser escaneado o confirmado
- el pagador confirma usando su cuenta fuente
- el sistema ejecuta la transaccion real

En terminos de negocio, esto puede representar una transferencia implicita por QR:

- el usuario que recibe dinero genera el QR
- el otro usuario lo paga
- la plataforma mueve el dinero entre cuentas internas del tenant

### Contrato del QR con frontend

La API no genera la imagen del QR.

La API crea y persiste el **intent**. El frontend es responsable de renderizar el QR visual.

El frontend deberia:

1. llamar al endpoint de creacion del intent
2. recibir el `QrTransactionIntentResponse`
3. generar la imagen QR localmente con una libreria de frontend
4. codificar dentro del QR un payload corto y estable

### Payload recomendado para el QR

No es recomendable meter datos sensibles completos dentro del QR.

Lo ideal es codificar uno de estos formatos:

- una URL del frontend
- un deep link de la app
- solo el `intentId`

Ejemplos:

```text
https://tuapp.com/qr/intent/{id}
financesystem://qr-intent/{id}
{ "intentId": "...", "type": "PAYMENT" }
```

### Que debe hacer el frontend al escanear

Cuando el QR sea escaneado por otro usuario, el frontend deberia:

1. leer el `intentId`
2. consultar el detalle del intent
3. mostrar monto, moneda, destino, descripcion y estado
4. permitir la confirmacion con la cuenta fuente del usuario autenticado

### Confirmacion del QR

La confirmacion se realiza contra:

```http
POST /api/me/transactions/qr/{id}/confirm
```

Ese paso:

- valida que el intent siga `PENDING`
- valida saldo, moneda, limites e idempotencia
- ejecuta la transaccion real como `PAYMENT`
- marca el intent como `CONFIRMED`

### Que pasa si se intenta reutilizar

- un intent confirmado no puede reutilizarse
- un mismo `idempotencyKey` no crea intents duplicados
- el backend rechaza confirmaciones repetidas o fuera de estado

Estados:

```text
PENDING
CONFIRMED
CANCELLED
EXPIRED
```

### Significado de cada estado QR

| Estado | Uso |
|---|---|
| `PENDING` | Intento creado y pendiente de confirmacion |
| `CONFIRMED` | El intento fue confirmado y ejecutado |
| `CANCELLED` | El intento fue cancelado |
| `EXPIRED` | El intento vencio antes de confirmarse |

### Reglas criticas del QR

- el intent se crea con monto fijo
- el monto no lo define el pagador en la confirmacion
- un intent confirmado no puede volver a ejecutarse
- el mismo `idempotencyKey` no crea intents duplicados
- el intent confirmado queda ligado a una transaccion real
- la ejecucion final actualiza saldo, contabilizacion y auditoria
- la API no dibuja el QR, solo entrega el intent
- el frontend genera la imagen QR con el payload devuelto
- el QR debe ser corto, estable y sin datos sensibles innecesarios

### Que representa y que no representa

**Representa**

- cobros QR
- pagos QR
- transferencia implicita entre usuarios o cuentas del mismo tenant

**No representa todavia**

- QR de monto abierto
- QR generico para retiro sin monto previo
- QR para transferencia cross-tenant
- QR para deposito con monto definido por el pagador

### Flujo completo del QR

1. El receptor genera un QR indicando cuenta destino, monto y moneda.
2. El sistema valida cuenta, moneda, limites e idempotencia.
3. El intent se guarda en estado `PENDING`.
4. El pagador usa su cuenta fuente para confirmar.
5. El sistema valida saldo, FX y limites de la cuenta fuente.
6. Se ejecuta la transaccion real como `PAYMENT`.
7. El intent cambia a `CONFIRMED` y queda trazado con la transaccion resultante.

### Por que el intent existe separado

Separar intent y transaccion real permite:

- evitar duplicidad de pagos
- mantener trazabilidad antes de mover dinero
- mostrar un estado intermedio al frontend
- cancelar o expirar intentos sin tocar saldos
- auditar quien creo el cobro y quien lo confirmo

### Respuesta del QR intent

La creacion del QR tambien debe devolver informacion util para que el frontend pueda renderizar el intento y su estado:

- `id`
- `status`
- `channel`
- `amount`
- `currency`
- `targetAccountId`
- `externalReference`
- `description`
- `idempotencyKey`
- `confirmedTransactionId`
- `createdAt`
- `updatedAt`

### Ejemplo de QR intent

```json
{
  "id": "5fcbcc73-8dd9-49d4-8a0b-2f7f8c6d7a09",
  "status": "PENDING",
  "channel": "QR",
  "amount": 100.00,
  "currency": "USD",
  "targetAccountId": "2a5d2e8e-1bc8-4b5f-a4f4-5d5e3c2d9f10",
  "externalReference": "ORDER-10021",
  "description": "Pago de servicios",
  "idempotencyKey": "qr-001",
  "confirmedTransactionId": null,
  "createdAt": "2026-05-16T15:10:00Z",
  "updatedAt": "2026-05-16T15:10:00Z"
}
```

Con eso el frontend puede:

- mostrar el QR pendiente
- refrescar su estado
- evitar reintentos manuales innecesarios
- navegar al pago ejecutado cuando ya fue confirmado

### Ejemplo de respuesta de transaccion

```json
{
  "id": "8f2d5f77-8a0f-4f4d-9f05-2e4d8fd4a111",
  "type": "TRANSFER",
  "status": "COMPLETED",
  "channel": "API",
  "amount": 10.00,
  "currency": "BOB",
  "sourceAccountNumber": "WAL-BOB-2026-000001",
  "targetAccountNumber": "WAL-USD-2026-000008",
  "failureReason": null,
  "idempotencyKey": "trx-001",
  "processedAt": "2026-05-16T15:12:00Z",
  "fxDetail": {
    "sourceCurrency": "BOB",
    "targetCurrency": "USD",
    "sourceAmount": 10.00,
    "targetAmountGross": 1.45,
    "targetAmountNet": 1.44,
    "exchangeRate": 6.90,
    "effectiveExchangeRate": 6.95,
    "feeAmount": 0.01,
    "feeCurrency": "USD",
    "feeType": "PERCENTAGE",
    "calculationMode": "SEPARATE",
    "feeIncludedInRate": false
  }
}
```

## Reglas actuales

- `deposit` y `transfer` convierten moneda si la cuenta destino usa otra moneda
- `withdrawal` y `payment` debitan desde la moneda real de la cuenta origen
- `hold` y `release` no convierten
- `refund` es completo, no parcial
- `reversal` opera sobre la transaccion original completa
- `QR intent` es una entidad separada para cobros y pagos QR
- un QR confirmado no puede reutilizarse
- un mismo QR no puede confirmarse dos veces
- `limits` se evalua antes de ejecutar
- `accounting` recibe metadata de FX y asientos balanceados
- idempotencia obligatoria

## Criterio de UI recomendado

El frontend debería mostrar la transaccion en tres bloques:

1. **Resumen**
   - tipo
   - estado
   - fecha
   - referencia

2. **Monto**
   - monto original
   - moneda original
   - monto neto
   - comision
   - tasa

3. **Participantes**
   - cuenta origen
   - cuenta destino
   - usuario solicitante
   - canal

## Como leer una respuesta de transaction

El frontend deberia usar estos campos para renderizar la operacion:

- `status`: estado actual de la transaccion
- `failureReason`: motivo explicito del error si fallo
- `fxDetail`: desglose de conversion y comision
- `sourceAccount` y `targetAccount`: origen y destino
- `sourceAccountNumber` y `targetAccountNumber`: numeros visibles de las cuentas
- `movements`: movimiento operativo y contable
- `idempotencyKey`: trazabilidad y reintentos seguros
- `processedAt`: fecha real de procesamiento
- `externalReference`: referencia externa o de negocio

## Endpoints actuales

```http
POST   /api/transactions/transfers
POST   /api/transactions/deposits
POST   /api/transactions/withdrawals
POST   /api/transactions/payments
POST   /api/transactions/holds
POST   /api/transactions/releases
POST   /api/transactions/fees
POST   /api/transactions/adjustments
POST   /api/transactions/qr/intents
POST   /api/transactions/{id}/reversal
POST   /api/transactions/{id}/refunds

GET    /api/transactions
GET    /api/transactions/{id}
GET    /api/accounts/{accountId}/transactions

POST   /api/me/transactions/deposits
POST   /api/me/transactions/transfers
POST   /api/me/transactions/withdrawals
POST   /api/me/transactions/payments
POST   /api/me/transactions/holds
POST   /api/me/transactions/releases
POST   /api/me/transactions/qr/{id}/confirm

GET    /api/me/transactions
GET    /api/me/transactions/{id}
```

## Respuesta enriquecida

La transaccion debe responder con informacion suficiente para frontend y soporte:

- `fxDetail`
- `sourceAccount`
- `targetAccount`
- `movements`
- `status`
- `failureReason`
- `idempotencyKey`
- `processedAt`
- `externalReference`

### `fxDetail`

| Campo | Uso |
|---|---|
| moneda origen | Moneda enviada en la solicitud |
| moneda destino | Moneda final de la cuenta afectada |
| monto origen | Monto original solicitado |
| monto bruto | Monto antes de comision |
| monto neto | Monto final aplicado |
| tasa base | Tasa configurada para el par de monedas |
| tasa efectiva | Tasa final cuando la comision esta incluida |
| comision | Cargo aplicado |
| tipo de comision | `NONE`, `FIXED` o `PERCENTAGE` |
| modo de calculo | `SEPARATE` o `INCLUDED` |
| flag de inclusion en tasa | Indica si la comision ya fue absorbida |

### Ejemplo de respuesta de transaccion

```json
{
  "id": "8f2d5f77-8a0f-4f4d-9f05-2e4d8fd4a111",
  "type": "TRANSFER",
  "status": "COMPLETED",
  "channel": "API",
  "amount": 10.00,
  "currency": "BOB",
  "sourceAccountNumber": "WAL-BOB-2026-000001",
  "targetAccountNumber": "WAL-USD-2026-000008",
  "failureReason": null,
  "idempotencyKey": "trx-001",
  "processedAt": "2026-05-16T15:12:00Z",
  "fxDetail": {
    "sourceCurrency": "BOB",
    "targetCurrency": "USD",
    "sourceAmount": 10.00,
    "targetAmountGross": 1.45,
    "targetAmountNet": 1.44,
    "exchangeRate": 6.90,
    "effectiveExchangeRate": 6.95,
    "feeAmount": 0.01,
    "feeCurrency": "USD",
    "feeType": "PERCENTAGE",
    "calculationMode": "SEPARATE",
    "feeIncludedInRate": false
  }
}
```

## Errores utiles

| Caso | Respuesta esperada |
|---|---|
| cuenta inexistente | `404` con mensaje claro |
| transaccion inexistente | `404` con mensaje claro |
| saldo insuficiente | `400` con requerido, disponible y moneda |
| moneda no soportada | `400` con mensaje de moneda invalida |
| cuenta cerrada o inactiva | `400` con motivo explicito |
| limite excedido | `400` o `403` segun la politica de la regla |
| permiso insuficiente | `403` con mensaje util de acceso denegado |
| idempotencyKey reutilizada con payload distinto | `409` con motivo de conflicto |
| qr ya confirmado | `400` o `409` con motivo de estado invalido |
| qr no pendiente | `400` con motivo claro |
| moneda del request incompatible con la regla de la operacion | `400` con mensaje de moneda o cuenta invalida |

---

# 3. FX y Comisiones

Modulo encargado de conversion de moneda y cargos por operacion.

Sirve para:

- depositar en una moneda distinta a la de la cuenta
- transferir entre cuentas con monedas diferentes
- calcular comision separada o integrada
- mostrar al frontend el detalle completo del calculo

## Request Bodies

### `POST /api/fx/rates`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `sourceCurrency` | `CurrencyCode` | Moneda origen del par | Enum rígido: `BOB`, `USD`, `EUR`, `USDT`. |
| `targetCurrency` | `CurrencyCode` | Moneda destino del par | Enum rígido: `BOB`, `USD`, `EUR`, `USDT`. |
| `rate` | `BigDecimal` | Tasa de cambio | Debe ser positiva y mayor que cero. |
| `active` | `boolean` | Activa o desactiva la tasa | Permite dejar una tasa sin uso sin borrarla. |
| `description` | `String` | Descripción opcional | Útil para identificar el origen de la tasa. |

### `PUT /api/fx/rates/{id}`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `sourceCurrency` | `CurrencyCode` | Moneda origen del par | Enum rígido: `BOB`, `USD`, `EUR`, `USDT`. |
| `targetCurrency` | `CurrencyCode` | Moneda destino del par | Enum rígido: `BOB`, `USD`, `EUR`, `USDT`. |
| `rate` | `BigDecimal` | Tasa actualizada | Debe ser positiva y mayor que cero. |
| `active` | `boolean` | Estado activo/inactivo | Activa o desactiva la tasa. |
| `description` | `String` | Descripción opcional | Ayuda a documentar la tasa. |

### `POST /api/fx/fees`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `operationCode` | `FxOperationCode` | Operación a la que aplica el fee | Enum rígido del core FX: `TRANSFER`, `CONVERSION`, `DEPOSIT`, `WITHDRAWAL`, `PAYMENT`. |
| `feeType` | `FeeType` | Tipo de comisión | Enum rígido: `NONE`, `FIXED`, `PERCENTAGE`. |
| `feeValue` | `BigDecimal` | Valor de la comisión | Puede ser cero cuando `feeType = NONE`. |
| `calculationMode` | `FeeCalculationMode` | Si se cobra aparte o dentro de la tasa | Enum rígido: `SEPARATE`, `INCLUDED`. |
| `active` | `boolean` | Activa o desactiva la configuración | Permite dejar el fee preparado para usarlo después. |
| `description` | `String` | Descripción opcional | Recomendado para frontend y administración. |

### `PUT /api/fx/fees/{id}`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `operationCode` | `FxOperationCode` | Operación a la que aplica el fee | Enum rígido del core FX: `TRANSFER`, `CONVERSION`, `DEPOSIT`, `WITHDRAWAL`, `PAYMENT`. |
| `feeType` | `FeeType` | Tipo de comisión | Enum rígido: `NONE`, `FIXED`, `PERCENTAGE`. |
| `feeValue` | `BigDecimal` | Valor de la comisión | Puede ser cero cuando `feeType = NONE`. |
| `calculationMode` | `FeeCalculationMode` | Si se cobra aparte o dentro de la tasa | Enum rígido: `SEPARATE`, `INCLUDED`. |
| `active` | `boolean` | Estado activo/inactivo | Habilita o deshabilita el fee. |
| `description` | `String` | Descripción opcional | Texto libre de soporte/administración. |

## Errores útiles de FX

| Caso | Respuesta esperada |
|---|---|
| tasa no encontrada | `404` con mensaje claro |
| fee no encontrado | `404` con mensaje claro |
| par de monedas duplicado | `409` o `400` con detalle del conflicto |
| tasa menor o igual a cero | `400` con mensaje de validación |
| fee sin operación válida | `400` indicando el `operationCode` incorrecto |
| fee con tipo `NONE` pero valor distinto de cero | `400` con motivo explicito |
| moneda no soportada | `400` con la moneda rechazada |

## Cómo entender estos cuerpos

- `sourceCurrency` y `targetCurrency` son enums, no strings libres.
- `FxOperationCode` es el catálogo de operaciones FX habilitadas por el core:
  - `TRANSFER`
  - `CONVERSION`
  - `DEPOSIT`
  - `WITHDRAWAL`
  - `PAYMENT`
- `rate` siempre es numérico y positivo.
- `feeValue` puede ser cero.
- `operationCode` identifica el tipo de operación afectada y llega como enum desde el frontend.
- `calculationMode` define si la comisión se ve por separado o ya embebida en la tasa.
- `feeCurrency` en el detalle de FX también es `CurrencyCode`.

## Arquitectura

```text
fx/
├── application
│   ├── dto
│   └── service
│
├── domain
│   ├── model
│   └── repository
│
└── infrastructure
    └── persistence
```

## Tipos y enums

### CurrencyCode

```text
BOB
USD
EUR
USDT
```

#### Significado

| Moneda | Uso |
|---|---|
| `BOB` | Bolivianos |
| `USD` | Dólares estadounidenses |
| `EUR` | Euros |
| `USDT` | Moneda estable soportada por el core |

### FxOperationCode

```text
TRANSFER
CONVERSION
DEPOSIT
WITHDRAWAL
PAYMENT
```

#### Significado

| Código | Uso |
|---|---|
| `TRANSFER` | Fee asociada a transferencias internas |
| `CONVERSION` | Fee asociada a conversiones directas |
| `DEPOSIT` | Fee asociada a depósitos |
| `WITHDRAWAL` | Fee asociada a retiros |
| `PAYMENT` | Fee asociada a pagos |

### FeeType

```text
NONE
FIXED
PERCENTAGE
```

#### Significado

| Tipo | Uso |
|---|---|
| `NONE` | No se cobra comision |
| `FIXED` | Se cobra un monto fijo |
| `PERCENTAGE` | Se cobra un porcentaje del monto |

### FeeCalculationMode

```text
SEPARATE
INCLUDED
```

#### Significado

| Modo | Uso |
|---|---|
| `SEPARATE` | La comision se calcula y muestra por separado |
| `INCLUDED` | La comision se absorbe dentro de la tasa efectiva |

### FxQuote

El `FxQuote` representa la cotizacion calculada para una operacion.

| Campo | Uso |
|---|---|
| `applied` | Si la cotizacion se aplico correctamente |
| `operationCode` | `FxOperationCode` que define la tarifa |
| `sourceCurrency` | `CurrencyCode` origen |
| `targetCurrency` | `CurrencyCode` destino |
| `sourceAmount` | Monto ingresado |
| `targetAmountGross` | Monto convertido antes de comision |
| `targetAmountNet` | Monto final despues de comision |
| `exchangeRate` | Tasa base |
| `effectiveExchangeRate` | Tasa resultante si la comision va incluida |
| `feeAmount` | Monto de comision |
| `feeCurrency` | `CurrencyCode` de cobro de la comision |
| `feeType` | Tipo de comision |
| `calculationMode` | Modo de calculo |
| `feeIncludedInRate` | Si la comision esta absorbida en la tasa |

### FxExchangeRateResponse

| Campo | Tipo | Uso |
|---|---|---|
| `sourceCurrency` | `CurrencyCode` | Moneda origen configurada |
| `targetCurrency` | `CurrencyCode` | Moneda destino configurada |
| `rate` | `BigDecimal` | Tasa base persistida |
| `active` | `boolean` | Estado de la tasa |
| `description` | `String` | Descripcion operativa |

### OperationFeeResponse

| Campo | Tipo | Uso |
|---|---|---|
| `operationCode` | `FxOperationCode` | Operacion a la que aplica la comision |
| `feeType` | `FeeType` | Tipo de comision |
| `feeValue` | `BigDecimal` | Valor configurado de la comision |
| `calculationMode` | `FeeCalculationMode` | Modo de calculo |
| `active` | `boolean` | Estado del fee |

### TransactionFxDetailResponse

| Campo | Tipo | Uso |
|---|---|---|
| `operationCode` | `FxOperationCode` | Operacion que genero el calculo |
| `sourceCurrency` | `CurrencyCode` | Moneda de entrada |
| `targetCurrency` | `CurrencyCode` | Moneda de salida |
| `sourceAmount` | `BigDecimal` | Monto original |
| `targetAmountGross` | `BigDecimal` | Monto convertido antes de comision |
| `targetAmountNet` | `BigDecimal` | Monto final despues de comision |
| `exchangeRate` | `BigDecimal` | Tasa base |
| `effectiveExchangeRate` | `BigDecimal` | Tasa efectiva luego de comision |
| `feeAmount` | `BigDecimal` | Monto cobrado como comision |
| `feeCurrency` | `CurrencyCode` | Moneda en la que se cobra la comision |
| `feeType` | `FeeType` | Tipo de comision |
| `calculationMode` | `FeeCalculationMode` | Modo de calculo |
| `feeIncludedInRate` | `boolean` | Si la comision ya esta absorbida en la tasa |

## Reglas actuales

- la tasa se configura por par de monedas
- la comision se configura por `operationCode`
- por defecto la comision es `0`
- `SEPARATE` muestra fee aparte
- `INCLUDED` absorbe la fee en la tasa efectiva
- el `OWNER_ADMIN` puede administrarlo

## Casos de uso del modulo

- depositar 10 BOB a una cuenta USD
- transferir USD a una cuenta EUR
- cobrar una tarifa fija por pago
- absorber la tarifa dentro del rate efectivo
- consultar la cotizacion antes de ejecutar una operacion

### Ejemplos JSON de frontend

#### Crear tasa FX

```json
{
  "sourceCurrency": "BOB",
  "targetCurrency": "USD",
  "rate": 6.9,
  "active": true,
  "description": "Default BOB to USD rate"
}
```

#### Crear fee FX

```json
{
  "operationCode": "TRANSFER",
  "feeType": "PERCENTAGE",
  "feeValue": 0.5,
  "calculationMode": "SEPARATE",
  "active": true,
  "description": "Transfer fee for standard retail users"
}
```

#### Actualizar fee FX con comisión incluida

```json
{
  "operationCode": "CONVERSION",
  "feeType": "FIXED",
  "feeValue": 0.1,
  "calculationMode": "INCLUDED",
  "active": true,
  "description": "Conversion fee absorbed in the effective rate"
}
```

### Ejemplo de quote FX

```json
{
  "applied": true,
  "operationCode": "TRANSFER",
  "sourceCurrency": "BOB",
  "targetCurrency": "USD",
  "sourceAmount": 10.00,
  "targetAmountGross": 1.45,
  "targetAmountNet": 1.44,
  "exchangeRate": 6.90,
  "effectiveExchangeRate": 6.95,
  "feeAmount": 0.01,
  "feeCurrency": "USD",
  "feeType": "PERCENTAGE",
  "calculationMode": "SEPARATE",
  "feeIncludedInRate": false
}
```

### Ejemplo de respuesta de tasa FX

```json
{
  "id": "1c9c1f0e-4f6a-4f4f-9f55-b0e4d4d7b2b1",
  "sourceCurrency": "BOB",
  "targetCurrency": "USD",
  "rate": 6.9,
  "active": true,
  "description": "Default BOB to USD rate",
  "createdAt": "2026-05-16T00:00:00Z",
  "updatedAt": "2026-05-16T00:00:00Z"
}
```

### Ejemplo de respuesta de fee FX

```json
{
  "id": "2f0d7f83-7f2b-4c7d-b7fa-ec3a0b94fd15",
  "operationCode": "TRANSFER",
  "feeType": "PERCENTAGE",
  "feeValue": 0.5,
  "calculationMode": "SEPARATE",
  "active": true,
  "description": "Transfer fee for standard retail users",
  "createdAt": "2026-05-16T00:00:00Z",
  "updatedAt": "2026-05-16T00:00:00Z"
}
```

## Que puede visualizar frontend

- tasa base
- tasa efectiva
- comision configurada
- comision aplicada en tiempo real
- resultado neto final
- modo de calculo usado

## Como interpretar la configuracion FX

La plataforma separa dos conceptos:

1. **Tasa de cambio**: convierte una moneda en otra.
2. **Comision**: agrega un cargo adicional a la operacion.

Eso permite que el frontend muestre claramente:

- monto original
- moneda original
- tasa aplicada
- comision cobrada
- monto convertido
- monto neto

## Seeders por defecto

- tasas entre `BOB`, `USD`, `EUR` y `USDT`
- fees base para:
  - `TRANSFER`
  - `CONVERSION`
  - `DEPOSIT`
  - `WITHDRAWAL`
  - `PAYMENT`

## Endpoints

```http
GET    /api/fx/rates
GET    /api/fx/rates/{id}
POST   /api/fx/rates
PUT    /api/fx/rates/{id}
DELETE /api/fx/rates/{id}

GET    /api/fx/fees
GET    /api/fx/fees/{id}
POST   /api/fx/fees
PUT    /api/fx/fees/{id}
DELETE /api/fx/fees/{id}
```

---

# 4. Limits

Modulo encargado de validar limites operativos y financieros.

Sirve para proteger al tenant de:

- montos excesivos
- abuso de operaciones
- volumen inusual
- reglas por usuario, cuenta o tipo de transaccion

## Request Bodies

### `POST /api/limits/rules`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `code` | `String` | Código único de la regla | Debe ser único dentro del tenant. |
| `name` | `String` | Nombre legible de la regla | Texto para frontend y administración. |
| `description` | `String` | Descripción de negocio | Opcional. |
| `limitType` | `LimitRuleType` | Tipo de límite | Enum rígido: `PER_TRANSACTION_AMOUNT`, `DAILY_AMOUNT`, `MONTHLY_AMOUNT`, `DAILY_COUNT`, `MONTHLY_COUNT`, `MIN_AMOUNT`, `MAX_BALANCE`. |
| `scopeType` | `LimitScopeType` | Alcance de la regla | Enum rígido: `TENANT`, `ACCOUNT_TYPE`, `TRANSACTION_TYPE`, `USER`, `ACCOUNT`. |
| `period` | `LimitPeriod` | Periodo de evaluación | Enum rígido: `TRANSACTION`, `DAILY`, `MONTHLY`. |
| `transactionType` | `TransactionType` | Tipo de transacción afectada | Enum rígido: `TRANSFER`, `DEPOSIT`, `WITHDRAWAL`, `PAYMENT`, `REVERSAL`, `REFUND`, `FEE`, `ADJUSTMENT`, `HOLD`, `RELEASE`, `SETTLEMENT`. Solo permitido si `scopeType = TRANSACTION_TYPE`. |
| `accountType` | `AccountType` | Tipo de cuenta afectada | Enum rígido: `WALLET`, `SAVINGS`, `CHECKING`, `CREDIT_CARD`, `PREPAID_CARD`, `LOAN`. Solo permitido si `scopeType = ACCOUNT_TYPE`. |
| `currency` | `CurrencyCode` | Moneda de aplicación | Enum rígido: `BOB`, `USD`, `EUR`, `USDT`. Opcional para reglas globales. |
| `minAmount` | `BigDecimal` | Monto mínimo permitido | Según el tipo de límite. |
| `maxAmount` | `BigDecimal` | Monto máximo permitido | Según el tipo de límite. |
| `maxCount` | `Long` | Límite de cantidad | Requerido en límites de conteo. |
| `active` | `boolean` | Regla activa/inactiva | Permite dejar la regla creada sin activar. |
| `requireReviewExceed` | `boolean` | Si exceder pide revisión en lugar de bloquear | Útil para flujo manual de aprobación. |

### `PATCH /api/limits/rules/{id}`

Usa los mismos campos que `POST`, pero todos son opcionales.
La API sigue validando consistencia entre `limitType`, `scopeType`, `period`, `transactionType`, `accountType` y `currency`.

### `POST /api/limits/evaluate`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `actorUserId` | `UUID` | Usuario que ejecuta la operación | Se usa para reglas por usuario. |
| `sourceAccountId` | `UUID` | Cuenta origen | Puede ser nulo según la operación. |
| `targetAccountId` | `UUID` | Cuenta destino | Puede ser nulo según la operación. |
| `sourceAccountType` | `AccountType` | Tipo de cuenta origen | Enum rígido: `WALLET`, `SAVINGS`, `CHECKING`, `CREDIT_CARD`, `PREPAID_CARD`, `LOAN`. |
| `targetAccountType` | `AccountType` | Tipo de cuenta destino | Enum rígido: `WALLET`, `SAVINGS`, `CHECKING`, `CREDIT_CARD`, `PREPAID_CARD`, `LOAN`. |
| `sourceAvailableBalance` | `BigDecimal` | Saldo disponible origen | Ayuda a límites por saldo máximo. |
| `targetAvailableBalance` | `BigDecimal` | Saldo disponible destino | Útil para topes de balance. |
| `transactionType` | `TransactionType` | Tipo de operación a evaluar | Enum rígido: `TRANSFER`, `DEPOSIT`, `WITHDRAWAL`, `PAYMENT`, `REVERSAL`, `REFUND`, `FEE`, `ADJUSTMENT`, `HOLD`, `RELEASE`, `SETTLEMENT`. |
| `currency` | `CurrencyCode` | Moneda de la operación | Enum rígido: `BOB`, `USD`, `EUR`, `USDT`. |
| `amount` | `BigDecimal` | Monto a evaluar | Valor que se comparará contra la regla. |

### Ejemplos JSON de frontend

#### Crear regla por monto diario para transferencias

```json
{
  "code": "daily-transfer-amount",
  "name": "Daily transfer amount",
  "description": "Daily amount limit for transfers",
  "limitType": "DAILY_AMOUNT",
  "scopeType": "TRANSACTION_TYPE",
  "period": "DAILY",
  "transactionType": "TRANSFER",
  "currency": "USD",
  "maxAmount": 500.00,
  "active": true,
  "requireReviewExceed": true
}
```

#### Crear regla por tipo de cuenta

```json
{
  "code": "wallet-per-tx",
  "name": "Wallet per transaction",
  "description": "Per transaction amount cap for wallet accounts",
  "limitType": "PER_TRANSACTION_AMOUNT",
  "scopeType": "ACCOUNT_TYPE",
  "period": "TRANSACTION",
  "accountType": "WALLET",
  "currency": "BOB",
  "maxAmount": 1000.00,
  "active": true,
  "requireReviewExceed": false
}
```

#### Evaluar una operación

```json
{
  "actorUserId": "b7b6c0f9-78bc-4f10-b2c8-7f8b1a7a1a33",
  "sourceAccountId": "5f8c2f8e-8c32-4f5f-9d9f-4d10f2dbe302",
  "targetAccountId": "0ab2d6de-1c70-47f1-ae1a-3cd6b7d72b91",
  "sourceAccountType": "WALLET",
  "targetAccountType": "SAVINGS",
  "sourceAvailableBalance": 2400.50,
  "targetAvailableBalance": 100.00,
  "transactionType": "TRANSFER",
  "currency": "USD",
  "amount": 150.00
}
```

## Cómo entender estos cuerpos

- `limitType` es el tipo de restricción real, por ejemplo monto por transacción o monto diario.
- `scopeType` dice dónde aplica la regla, por ejemplo usuario, cuenta o tenant.
- `period` define cómo se acumula el uso.
- `requireReviewExceed` permite pedir aprobación cuando el límite se excede.
- `transactionType`, `accountType` y `currency` llegan como enums desde el frontend, no como strings libres.
- `transactionType` solo tiene sentido cuando la regla quiere amarrarse a un tipo concreto de operación.
- `accountType` solo tiene sentido cuando la regla quiere amarrarse a un tipo concreto de cuenta.
- `currency` puede dejarse vacío si la regla es global o si no depende de moneda.

## Tipos de respuesta

### `LimitRuleResponse`

La API devuelve estos campos como enums:

| Campo | Tipo |
|---|---|
| `limitType` | `LimitRuleType` |
| `scopeType` | `LimitScopeType` |
| `period` | `LimitPeriod` |
| `transactionType` | `TransactionType` |
| `accountType` | `AccountType` |
| `currency` | `CurrencyCode` |

### `LimitEvaluationResponse`

| Campo | Tipo | Uso |
|---|---|---|
| `transactionType` | `TransactionType` | Operación evaluada |
| `currency` | `CurrencyCode` | Moneda evaluada |
| `checks` | `List<LimitEvaluationRuleCheckResponse>` | Detalle por regla |

### `LimitEvaluationRuleCheckResponse`

| Campo | Tipo | Uso |
|---|---|---|
| `limitType` | `LimitRuleType` | Tipo de regla evaluada |
| `scopeType` | `LimitScopeType` | Alcance de la regla |
| `period` | `LimitPeriod` | Periodo de la regla |
| `currentAmount` | `BigDecimal` | Acumulado actual |
| `currentCount` | `Long` | Conteo actual |
| `remainingAmount` | `BigDecimal` | Disponible restante |
| `remainingCount` | `Long` | Operaciones restantes |
| `maxAmount` | `BigDecimal` | Tope de monto |
| `maxCount` | `Long` | Tope de conteo |

## Tipos y enums

### LimitRuleType

```text
PER_TRANSACTION_AMOUNT
DAILY_AMOUNT
MONTHLY_AMOUNT
DAILY_COUNT
MONTHLY_COUNT
MIN_AMOUNT
MAX_BALANCE
```

#### Significado

| Tipo | Uso |
|---|---|
| `PER_TRANSACTION_AMOUNT` | Limite por operacion individual |
| `DAILY_AMOUNT` | Limite acumulado por monto diario |
| `MONTHLY_AMOUNT` | Limite acumulado por monto mensual |
| `DAILY_COUNT` | Limite por cantidad diaria de operaciones |
| `MONTHLY_COUNT` | Limite por cantidad mensual de operaciones |
| `MIN_AMOUNT` | Monto minimo permitido |
| `MAX_BALANCE` | Saldo maximo permitido en una cuenta |

### LimitScopeType

```text
TENANT
ACCOUNT_TYPE
TRANSACTION_TYPE
USER
ACCOUNT
```

#### Significado

| Scope | Uso |
|---|---|
| `TENANT` | Regla global para todo el tenant |
| `ACCOUNT_TYPE` | Regla por tipo de cuenta |
| `TRANSACTION_TYPE` | Regla por tipo de transaccion |
| `USER` | Regla por usuario especifico |
| `ACCOUNT` | Regla por cuenta especifica |

### LimitPeriod

```text
TRANSACTION
DAILY
MONTHLY
```

#### Significado

| Periodo | Uso |
|---|---|
| `TRANSACTION` | Evaluacion instantanea |
| `DAILY` | Acumulado diario |
| `MONTHLY` | Acumulado mensual |

## Reglas actuales

- si no hay reglas que coincidan, la operacion se permite
- si una regla coincide y excede el limite, puede bloquear o pedir revision
- se registra uso por periodo y scope
- soporta multiples monedas
- es administrable por `OWNER_ADMIN`
- la evaluacion devuelve enums reales en `transactionType`, `currency`, `limitType`, `scopeType` y `period`
- `PENDING_REVIEW` ya no es parte del flujo operativo activo; si un limite pide revision, hoy la operacion se rechaza con un mensaje util

## Que puede hacer el frontend con limits

- mostrar si una operacion requiere revision
- explicar por que se bloqueo una operacion
- mostrar cuanto queda disponible antes de exceder el limite
- mostrar acumulados diarios o mensuales
- mostrar reglas activas por cuenta o tipo de transaccion

## Como se interpreta en frontend

La respuesta de evaluacion debe permitir mostrar:

- si la operacion esta permitida
- si requiere revision manual
- que regla la bloqueo
- cual es el acumulado actual
- cual es el limite configurado
- a que scope aplica la regla

### Ejemplo de evaluacion de limite

```json
{
  "allowed": false,
  "requiresReview": true,
  "reason": "Daily transfer amount exceeded",
  "checks": [
    {
      "ruleId": "d1b7b5d8-9af4-4c49-9d22-0e3f4d1a3c11",
      "matched": true,
      "allowed": false,
      "reason": "Amount exceeds the configured aggregated limit",
      "currentAmount": 100.00,
      "currentCount": 2,
      "remainingAmount": 0.00,
      "remainingCount": 0,
      "maxAmount": 50.00,
      "maxCount": null,
      "scopeType": "USER",
      "limitType": "DAILY_AMOUNT",
      "period": "DAILY"
    }
  ]
}
```

## Errores útiles de Limits

| Caso | Respuesta esperada |
|---|---|
| regla no encontrada | `404` con mensaje claro |
| código de regla duplicado | `409` o `400` con detalle |
| combinación inconsistente de `limitType`, `scopeType` y `period` | `400` con motivo explicito |
| `transactionType` enviado cuando no corresponde | `400` indicando el campo incorrecto |
| `accountType` enviado cuando no corresponde | `400` indicando el campo incorrecto |
| evaluación excedida | `400` o `403` con motivo útil |
| regla inactiva no coincide | `200` con `allowed = true` si no participa en la evaluación |

### Tipos de respuesta de limits

#### `LimitRuleResponse`

La API devuelve estos campos con tipos rígidos:

| Campo | Tipo | Uso |
|---|---|---|
| `limitType` | `LimitRuleType` | Tipo real de restricción |
| `scopeType` | `LimitScopeType` | Alcance de la regla |
| `period` | `LimitPeriod` | Periodo de acumulación |
| `transactionType` | `TransactionType` | Operación a la que aplica, si corresponde |
| `accountType` | `AccountType` | Tipo de cuenta afectada, si corresponde |
| `currency` | `CurrencyCode` | Moneda de aplicación, si corresponde |

#### `LimitEvaluationResponse`

| Campo | Tipo | Uso |
|---|---|---|
| `transactionType` | `TransactionType` | Operación evaluada |
| `currency` | `CurrencyCode` | Moneda evaluada |
| `checks` | `List<LimitEvaluationRuleCheckResponse>` | Detalle por regla |

#### `LimitEvaluationRuleCheckResponse`

| Campo | Tipo | Uso |
|---|---|---|
| `limitType` | `LimitRuleType` | Tipo de regla evaluada |
| `scopeType` | `LimitScopeType` | Alcance de la regla |
| `period` | `LimitPeriod` | Periodo de la regla |
| `reason` | `String` | Motivo de bloqueo o aprobación |
| `currentAmount` | `BigDecimal` | Acumulado actual |
| `currentCount` | `Long` | Conteo actual |
| `remainingAmount` | `BigDecimal` | Saldo restante dentro del límite |
| `remainingCount` | `Long` | Operaciones restantes dentro del límite |
| `maxAmount` | `BigDecimal` | Tope configurado |
| `maxCount` | `Long` | Conteo máximo configurado |

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

# 5. Accounting

Modulo encargado de la contabilidad formal del tenant.

`accounts` muestra el saldo operacional.
`accounting` conserva la verdad contable.

## Request Bodies

### `POST /api/accounting/periods`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `periodCode` | `String` | Código único del periodo | Debe ser único en el tenant. |
| `periodType` | `AccountingPeriodType` | Tipo de periodo | Enum rígido: `DAILY`, `MONTHLY`, `ANNUAL`, `CUSTOM`. |
| `startDate` | `LocalDate` | Fecha inicial | Fecha de apertura del periodo. |
| `endDate` | `LocalDate` | Fecha final | No puede ser anterior a `startDate`. |
| `description` | `String` | Descripción opcional | Útil para operación y auditoría. |

### `PATCH /api/accounting/periods/{id}/close`

| Campo | Tipo | Uso | Notas |
|---|---|---|---|
| `reason` | `String` | Motivo del cierre | Recomendado para dejar trazabilidad operativa. |

## Errores útiles de Accounting

| Caso | Respuesta esperada |
|---|---|
| periodo no encontrado | `404` con mensaje claro |
| periodo ya cerrado | `400` o `409` con motivo explícito |
| periodo con fechas inválidas | `400` con detalle de validación |
| asiento no balanceado | `400` con el motivo contable |
| periodo cerrado impide posteo | `400` con el estado real del periodo |
| request incompleta | `400` con el campo faltante o nulo |

## Cómo entender estos cuerpos

- `periodCode` es una referencia humana o interna para ubicar el periodo.
- `periodType` es un enum de negocio, no un texto libre.
- `reason` se usa para dejar trazabilidad del cierre.

## Arquitectura

```text
accounting/
├── application
│   ├── dto
│   ├── mapper
│   └── usecase
│
├── domain
│   ├── exception
│   ├── model
│   └── repository
│
└── infrastructure
    ├── api
    ├── adapter
    └── persistence
```

## Tipos y enums

### JournalEntryType

```text
TRANSFER
DEPOSIT
WITHDRAWAL
PAYMENT
REVERSAL
REFUND
HOLD
RELEASE
FEE
ADJUSTMENT
SETTLEMENT
```

#### Significado

| Tipo | Uso |
|---|---|
| `TRANSFER` | Asiento generado por una transferencia |
| `DEPOSIT` | Asiento generado por un deposito |
| `WITHDRAWAL` | Asiento generado por un retiro |
| `PAYMENT` | Asiento generado por un pago |
| `REVERSAL` | Asiento que revierte otro asiento |
| `REFUND` | Asiento por devolucion total |
| `HOLD` | Asiento por retencion de saldo |
| `RELEASE` | Asiento por liberacion de retencion |
| `FEE` | Asiento por comision |
| `ADJUSTMENT` | Asiento por ajuste administrativo |
| `SETTLEMENT` | Asiento por liquidacion o compensacion futura |

### JournalEntryStatus

```text
DRAFT
POSTED
REVERSED
VOID
```

#### Significado

| Estado | Uso |
|---|---|
| `DRAFT` | Asiento creado pero no posteado |
| `POSTED` | Asiento contabilizado |
| `REVERSED` | Asiento revertido |
| `VOID` | Asiento anulado |

### JournalLineType

```text
DEBIT
CREDIT
```

#### Significado

| Tipo | Uso |
|---|---|
| `DEBIT` | Movimiento de cargo o salida en la partida doble |
| `CREDIT` | Movimiento de abono o entrada en la partida doble |

### AccountingPeriodType

```text
DAILY
MONTHLY
ANNUAL
CUSTOM
```

#### Significado

| Tipo | Uso |
|---|---|
| `DAILY` | Periodo diario |
| `MONTHLY` | Periodo mensual |
| `ANNUAL` | Periodo anual |
| `CUSTOM` | Periodo definido manualmente |

### AccountingPeriodStatus

```text
OPEN
CLOSED
ARCHIVED
```

#### Significado

| Estado | Uso |
|---|---|
| `OPEN` | Se pueden postear asientos |
| `CLOSED` | Ya no se permite registrar movimientos nuevos |
| `ARCHIVED` | Periodo historico conservado |

## Reglas actuales

- cada transaccion completada genera un asiento contable
- el asiento debe quedar balanceado
- las lineas reflejan debit y credit
- el periodo contable debe estar abierto para postear
- `SETTLEMENT` queda como concepto contable, no como endpoint publico del core

## Para que sirve accounting en la operacion diaria

- conciliacion
- auditoria financiera
- cierres contables
- reportes confiables
- trazabilidad por periodo

## Que debe entender el frontend de accounting

- no es el saldo operativo inmediato
- es el soporte formal de libro mayor
- sirve para verificar que una transaccion tuvo impacto contable
- permite mostrar periodos abiertos y cerrados
- permite auditar asientos por tipo y estado

### Ejemplo de asiento contable

```json
{
  "id": "3aa4b2d1-0b1a-4d11-8db0-cd99e4d90010",
  "type": "TRANSFER",
  "status": "POSTED",
  "periodId": "d4f2b4a2-11d3-44b9-9a9b-1f3c2a9f2a21",
  "postedAt": "2026-05-16T15:12:01Z",
  "lines": [
    {
      "lineType": "DEBIT",
      "accountCode": "110100",
      "amount": 1.44,
      "currency": "USD"
    },
    {
      "lineType": "CREDIT",
      "accountCode": "210200",
      "amount": 1.44,
      "currency": "USD"
    }
  ]
}
```

## Endpoints

```http
GET    /api/accounting/periods
POST   /api/accounting/periods
PATCH  /api/accounting/periods/{id}/close

GET    /api/accounting/journal-entries
GET    /api/accounting/journal-entries/{id}
```

---

# 6. Governance

Modulo encargado de auditoria, trazabilidad y eventos del sistema.

## Responsabilidades

- auditar acciones sensibles
- registrar login y fallos
- registrar cambios de roles y permisos
- registrar exportaciones
- registrar transacciones completadas
- mantener trazabilidad completa
- servir como base para notificaciones y alertas operativas

## Metadatos utiles de auditoria

Cada evento de governance puede guardar:

- usuario que ejecuta
- tenant
- IP origen
- user-agent
- entidad afectada
- valor anterior y nuevo
- referencia de transaccion
- fecha y hora

Eso permite reconstruir la accion completa para soporte, auditoria y seguridad.

## Tipos de eventos frecuentes

```text
LOGIN
LOGIN_FAILED
ACCOUNT_CREATED
ACCOUNT_BLOCKED
TRANSFER
PAYMENT
ROLE_ASSIGNMENT
EXPORT
PASSWORD_CHANGE
TRANSACTION_COMPLETED
```

### Significado

| Evento | Uso |
|---|---|
| `LOGIN` | Inicio de sesion exitoso |
| `LOGIN_FAILED` | Intento de acceso fallido |
| `ACCOUNT_CREATED` | Se creo una cuenta |
| `ACCOUNT_BLOCKED` | Se bloqueo una cuenta |
| `TRANSFER` | Se ejecuto una transferencia |
| `PAYMENT` | Se ejecuto un pago |
| `ROLE_ASSIGNMENT` | Cambio de roles o permisos |
| `EXPORT` | Exportacion de informacion |
| `PASSWORD_CHANGE` | Cambio de credenciales |
| `TRANSACTION_COMPLETED` | Finalizacion exitosa de una transaccion |

## Endpoints

```http
GET    /api/audit-events
GET    /api/audit-events/{id}
GET    /api/system-events
```

---

# 7. Notifications

Modulo encargado del inbox operativo, dispositivos push, preferencias y plantillas.

## Resumen rapido

| Concepto | Uso |
|---|---|
| `Notification` | Notificacion real persistida en base de datos |
| `NotificationDelivery` | Intento de entrega por canal |
| `NotificationDevice` | Dispositivo registrado del usuario |
| `NotificationPreference` | Preferencias por categoria |
| `NotificationTemplate` | Plantilla reusable para mensajes |

La regla principal de este modulo es simple:

- la notificacion real se guarda primero en base de datos
- el push es un derivado de esa notificacion
- si el push falla, la notificacion in-app sigue existiendo
- si no hay Firebase o no hay dispositivo, la notificacion igual queda disponible para frontend

## Objetivo funcional

Este modulo permite que el frontend muestre:

- campanita o inbox
- contador de no leidas
- detalle de una notificacion
- marcar como leida
- marcar como abierta
- archivar
- administrar dispositivos registrados
- administrar preferencias por categoria

Tambien permite a soporte o administracion revisar:

- templates
- deliveries
- estado de una entrega push

## Relacion con el core

`transactions`, `accounts` y `auth` publican eventos de negocio y `notifications` los transforma en mensajes utiles para el usuario.

Ejemplos:

- una transaccion completada genera una notificacion de transaccion
- una cuenta aprobada, bloqueada o cerrada genera una notificacion de cuenta
- un cambio de contrasena o restablecimiento genera una notificacion de seguridad

## Principios de diseno

- `Notification` es la verdad del sistema
- `NotificationDelivery` es la evidencia de intento de entrega
- `NotificationDevice` representa un dispositivo registrado del usuario
- `NotificationPreference` define que canales desea recibir por categoria
- `NotificationTemplate` permite estandarizar mensajes por tipo y canal

## Tipos y estados

### `NotificationType`

Catalogo rigido de eventos que puede mostrar el inbox.

Incluye, entre otros:

- `TRANSFER_RECEIVED`
- `TRANSFER_SENT`
- `PAYMENT_SUCCESS`
- `PAYMENT_FAILED`
- `DEPOSIT_CONFIRMED`
- `WITHDRAWAL_CONFIRMED`
- `REFUND_COMPLETED`
- `REVERSAL_COMPLETED`
- `QR_CONFIRMED`
- `ACCOUNT_BLOCKED`
- `ACCOUNT_APPROVED`
- `ACCOUNT_ACTIVATED`
- `ACCOUNT_FROZEN`
- `ACCOUNT_CLOSED`
- `LOGIN_ALERT`
- `SECURITY_ALERT`
- `SYSTEM_NOTICE`
- `LIMIT_REVIEW_REQUIRED`
- `LIMIT_EXCEEDED`
- `FX_RATE_UPDATED`
- `FEE_UPDATED`
- `PASSWORD_RESET_REQUESTED`
- `PASSWORD_RESET_COMPLETED`
- `PASSWORD_CHANGED`

### `NotificationCategory`

Agrupa la notificacion por dominio funcional:

- `TRANSACTIONS`
- `PAYMENTS`
- `ACCOUNTS`
- `SECURITY`
- `SYSTEM`
- `FX`
- `LIMITS`
- `ADMIN`

### `NotificationPriority`

Define la urgencia visual y operativa:

- `LOW`
- `NORMAL`
- `HIGH`
- `CRITICAL`

### `NotificationStatus`

Estado logico para el inbox:

- `UNREAD`
- `READ`
- `ARCHIVED`
- `EXPIRED`

### `NotificationChannel`

Canal de salida o visualizacion:

- `PUSH`
- `IN_APP`
- `EMAIL`
- `SMS`

### `NotificationDeviceStatus`

Estado del dispositivo registrado:

- `ACTIVE`
- `INACTIVE`
- `EXPIRED`
- `REVOKED`

### `NotificationPlatform`

Plataforma del dispositivo:

- `ANDROID`
- `IOS`
- `WEB`
- `DESKTOP`
- `UNKNOWN`

### `NotificationDeliveryStatus`

Estado del intento de entrega:

- `PENDING`
- `SENT`
- `FAILED`
- `SKIPPED`
- `EXPIRED`

## Endpoints de usuario

### Resumen de rutas

| Metodo | Ruta | Uso |
|---|---|---|
| `POST` | `/api/me/notification-devices` | Registrar o actualizar dispositivo |
| `GET` | `/api/me/notification-devices` | Listar dispositivos |
| `PATCH` | `/api/me/notification-devices/{id}/deactivate` | Desactivar dispositivo |
| `DELETE` | `/api/me/notification-devices/{id}` | Revocar dispositivo |
| `GET` | `/api/me/notifications` | Listar notificaciones |
| `GET` | `/api/me/notifications/unread-count` | Contar no leidas |
| `GET` | `/api/me/notifications/{id}` | Ver detalle |
| `PATCH` | `/api/me/notifications/{id}/read` | Marcar leida |
| `PATCH` | `/api/me/notifications/{id}/open` | Marcar abierta |
| `PATCH` | `/api/me/notifications/read-all` | Marcar todas leidas |
| `PATCH` | `/api/me/notifications/{id}/archive` | Archivar |
| `GET` | `/api/me/notification-preferences` | Listar preferencias |
| `PUT` | `/api/me/notification-preferences` | Actualizar preferencias |

### `POST /api/me/notification-devices`

Registra o actualiza un dispositivo del usuario autenticado.

Campos:

- `deviceId`
- `fcmToken`
- `platform`
- `deviceName`
- `appVersion`
- `osVersion`

El `userId` no se envía en el body: el backend lo resuelve desde el token del usuario autenticado.

### `GET /api/me/notification-devices`

Lista dispositivos registrados del usuario.

### `PATCH /api/me/notification-devices/{id}/deactivate`

Desactiva un dispositivo sin borrarlo fisicamente.

### `DELETE /api/me/notification-devices/{id}`

Revoca un dispositivo.

### `GET /api/me/notifications`

Lista notificaciones del usuario autenticado.

Parámetro:

- `limit`

### `GET /api/me/notifications/unread-count`

Devuelve el contador de notificaciones no leidas.

### `GET /api/me/notifications/{id}`

Devuelve el detalle completo de una notificacion.

### `PATCH /api/me/notifications/{id}/read`

Marca una notificacion como leida.

### `PATCH /api/me/notifications/{id}/open`

Marca una notificacion como abierta.

### `PATCH /api/me/notifications/read-all`

Marca como leidas todas las notificaciones pendientes del usuario.

### `PATCH /api/me/notifications/{id}/archive`

Archiva una notificacion.

### `GET /api/me/notification-preferences`

Lista preferencias del usuario por categoria.

### `PUT /api/me/notification-preferences`

Actualiza preferencias del usuario por categoria.

Campos:

- `category`
- `pushEnabled`
- `inAppEnabled`
- `emailEnabled`
- `smsEnabled`

El `userId` no se envía en el body: el backend lo resuelve desde el token del usuario autenticado.

## Endpoints administrativos

### Resumen de rutas

| Metodo | Ruta | Uso |
|---|---|---|
| `GET` | `/api/notifications/templates` | Listar templates |
| `GET` | `/api/notifications/templates/{id}` | Ver template |
| `GET` | `/api/notifications/{notificationId}/deliveries` | Ver deliveries de una notificacion |

### `GET /api/notifications/templates`

Lista templates activos o historicos del tenant.

### `GET /api/notifications/templates/{id}`

Devuelve un template especifico.

### `GET /api/notifications/{notificationId}/deliveries`

Lista los intentos de entrega de una notificacion.

## Despliegue de Push

La capa de push es opcional y no debe bloquear el funcionamiento del inbox ni de las operaciones bancarias.

### Variables de entorno

La configuracion nueva vive en `/.env.notification` y debe copiarse al archivo de entorno real que use el backend:

- `NOTIFICATIONS_PUSH_ENABLED`
- `NOTIFICATIONS_PROVIDER`
- `FIREBASE_CREDENTIALS_PATH`
- `FIREBASE_PROJECT_ID`

### Contrato de uso

- `NOTIFICATIONS_PUSH_ENABLED=false` mantiene el inbox funcionando sin intentar push real.
- `NOTIFICATIONS_PROVIDER=FIREBASE` activa el provider Firebase.
- `NOTIFICATIONS_PROVIDER=NOOP` deja el provider inactivo aunque el modulo siga operativo.
- `FIREBASE_CREDENTIALS_PATH` debe apuntar al JSON real de service account cuando Firebase ya este listo.
- el JSON real no debe guardarse en el repositorio.
- si Firebase falla al inicializar o al enviar, la notificacion real sigue guardada y el fallo queda solo en `deliveries`.

### Escenarios recomendados

- desarrollo local sin credenciales: `NOTIFICATIONS_PUSH_ENABLED=false`
- desarrollo con credenciales reales: `NOTIFICATIONS_PUSH_ENABLED=true`
- produccion: `NOTIFICATIONS_PUSH_ENABLED=true` y `FIREBASE_CREDENTIALS_PATH` apuntando al secret montado por Docker o por el orquestador

## Contrato de uso

- el frontend no genera la notificacion real
- el frontend solo consume el inbox y, si aplica, genera la representacion visual del QR o deeplink que venga en `actionUrl`
- el push solo es una forma de aviso, no la fuente de verdad
- si el usuario no tiene dispositivos activos, la notificacion sigue existiendo en `IN_APP`

## Casos de uso frecuentes

- alerta de deposito confirmado
- alerta de transferencia recibida
- alerta de cuenta aprobada
- alerta de cuenta bloqueada
- alerta de cambio de contrasena
- alerta de restablecimiento de contrasena
- alerta de limite excedido
- alerta de tipo de cambio actualizado

## Errores utiles

| Caso | Respuesta esperada |
|---|---|
| notificacion no encontrada | `404` |
| no pertenece al usuario autenticado | `404` o `403` segun la politica |
| dispositivo no encontrado | `404` |
| device token invalido | `400` |
| preference category invalida | `400` |
| push deshabilitado | la notificacion sigue existiendo y se registra `SKIPPED` |

## Relacion con frontend

El frontend debe tratar este modulo como un inbox real:

- mostrar unread count
- mostrar badges por categoria
- permitir marcar como leida
- permitir archivar
- mostrar detalle completo
- registrar dispositivos en login o primer acceso
- mostrar preferencias por categoria

La notificacion real siempre es la fuente de verdad.

---

# 8. Reporting

Modulo de consultas, dashboards y exportacion.

## Estado actual

Este modulo debe crecer despues del core operativo. La base arquitectonica ya existe como objetivo, pero no debe bloquear `accounts` ni `transactions`.

Con la base actual, reporting no es obligatorio para operar un banco tenant, pero si para escalar analitica, supervision y direccion.

## Para quien es reporting

- gerencia
- operacion
- contabilidad
- soporte
- analitica
- cumplimiento

## Que puede mostrar en el futuro

- volumen transaccional por periodo
- saldos por tipo de cuenta
- conversiones por moneda
- comisiones recaudadas
- cuentas activas vs bloqueadas
- actividad por usuario o segmento

## Tipos de reporte

```text
FINANCIAL
TRANSACTIONAL
ACCOUNTING
USERS
CUSTOM
```

### Significado

| Tipo | Uso |
|---|---|
| `FINANCIAL` | Resumen de saldos, volumen y resultados |
| `TRANSACTIONAL` | Analitica de operaciones y volumen |
| `ACCOUNTING` | Reportes contables y de periodos |
| `USERS` | Reportes de usuarios y actividad |
| `CUSTOM` | Reportes parametrizables |

## Funciones futuras

- dashboards
- resumenes financieros
- exportacion CSV / XLSX / PDF
- consultas historicas
- indicadores operativos

---

# 9. Propuesta de Cross-Tenant (implementacion propuesta)

La transferencia entre tenants distintos no debe vivir dentro de `transactions`.

Debe ser una capa de plataforma que coordina dos tenants.

Esto se documenta para la siguiente fase de producto, pero no forma parte del core operativo actual.

## Propuesta

```text
platform/
└── intertenant-transfers
```

## Flujo sugerido

1. El tenant origen solicita la transferencia.
2. La plataforma valida origen, destino, moneda y monto.
3. Se reserva o debita el monto en el origen.
4. Se acredita el destino.
5. Se confirma o compensa si hay error.

## Estados sugeridos

```text
PENDING
RESERVED
PROCESSING
COMPLETED
FAILED
REVERSED
CANCELLED
SETTLED
```

### Significado

| Estado | Uso |
|---|---|
| `PENDING` | La solicitud fue creada |
| `RESERVED` | Los fondos quedaron reservados en origen |
| `PROCESSING` | La operacion esta moviendose entre tenants |
| `COMPLETED` | Origen y destino quedaron confirmados |
| `FAILED` | La operacion fallo |
| `REVERSED` | La operacion fue revertida |
| `CANCELLED` | Se cancelo antes de completar |
| `SETTLED` | Liquidacion final o compensacion interna |

## Reglas sugeridas

- el origen es la fuente de verdad del dinero debitado
- el destino usa la tasa vigente si hay conversion
- el detalle FX debe persistirse
- si falla el destino, se libera el origen
- debe existir trazabilidad total

## Por que cross-tenant va aparte

- cruza dos schemas o contextos distintos
- requiere compensacion si un lado falla
- necesita trazabilidad de origen y destino
- puede requerir colas o eventos mas adelante
- no debe contaminar la operacion normal de un tenant

## Permisos futuros

```text
intertenant.transfers.create
intertenant.transfers.read
intertenant.transfers.cancel
intertenant.transfers.reverse
intertenant.transfers.settle
```

---

# Modulos pendientes

```text
kyc            -> verificacion de identidad
aml            -> prevencion de lavado
risk           -> antifraude
cards          -> tarjetas
beneficiaries  -> destinatarios frecuentes
loans          -> prestamos
statements     -> extractos
cross-tenant   -> transferencias entre tenants
settlements    -> compensacion futura
```

## Como leer esta lista

Estos modulos no son obligatorios para operar el banco base, pero si para llevarlo a una fase mas madura:

- `kyc` y `aml` para cumplimiento
- `risk` para fraude y scoring
- `cards` para medios de pago
- `beneficiaries` para destinatarios frecuentes
- `loans` para creditos
- `statements` para extractos
- `cross-tenant` para transferencias entre bancos tenant
- `settlements` para compensacion futura

## Orden real de crecimiento sugerido

1. `accounts`
2. `transactions`
3. `fx`
4. `limits`
5. `accounting`
6. `governance`
7. `notifications`
8. `reporting`
9. `cross-tenant`
10. `kyc` / `aml`
11. `risk`
12. `cards`
13. `beneficiaries`
14. `loans`
15. `statements`

---

# Criterio de construccion

Orden recomendado:

1. `accounts`
2. `transactions`
3. `fx`
4. `limits`
5. `accounting`
6. `governance`
7. `notifications`
8. `reporting`
9. `cross-tenant`
10. `kyc`, `aml`, `risk`, `cards`, `beneficiaries`, `loans`
