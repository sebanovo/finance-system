Basado en la documentación, estos son los **endpoints más importantes** para probar el flujo bancario core. Te los organizo por orden lógico de uso.

## Configuración Postman

**Variables de entorno a configurar:**
```
base_url = http://localhost:8080
tenant_id = {id_del_tenant}
user_token = {token_jwt_del_usuario}
admin_token = {token_jwt_del_owner_admin}
```

**Headers comunes:**
```
Content-Type: application/json
X-Tenant-ID: {{tenant_id}}
Authorization: Bearer {{user_token}}  (o {{admin_token}})
```

---

## 1. Gestión de Cuentas (Accounts)

### Crear cuenta (como usuario)
```http
POST {{base_url}}/api/me/accounts
```

**Body:**
```json
{
  "accountName": "MAIN_WALLET",
  "customAlias": "Mi billetera principal",
  "accountType": "WALLET",
  "currency": "BOB"
}
```

### Crear cuenta (como admin)
```http
POST {{base_url}}/api/accounts
```

**Body:**
```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "accountName": "SAVINGS_ACCOUNT",
  "customAlias": "Ahorros viaje",
  "accountType": "SAVINGS",
  "currency": "USD",
  "primary": false
}
```

### Listar mis cuentas
```http
GET {{base_url}}/api/me/accounts
```

### Ver balance de una cuenta
```http
GET {{base_url}}/api/me/accounts/{accountId}/balance
```

**Respuesta esperada:**
```json
{
  "accountId": "abc-123",
  "availableBalance": 1000.00,
  "heldBalance": 0.00,
  "totalBalance": 1000.00,
  "currency": "BOB"
}
```

---

## 2. FX y Comisiones (Configuración inicial)

**Nota:** Estos endpoints requieren token de `OWNER_ADMIN`

### Crear tasa de cambio
```http
POST {{base_url}}/api/fx/rates
```

**Body:**
```json
{
  "sourceCurrency": "BOB",
  "targetCurrency": "USD",
  "rate": 6.90,
  "active": true,
  "description": "Tasa oficial BOB/USD"
}
```

### Crear comisión para transferencias
```http
POST {{base_url}}/api/fx/fees
```

**Body:**
```json
{
  "operationCode": "TRANSFER",
  "feeType": "PERCENTAGE",
  "feeValue": 0.5,
  "calculationMode": "SEPARATE",
  "active": true,
  "description": "Comisión 0.5% en transferencias"
}
```

### Consultar tasas activas
```http
GET {{base_url}}/api/fx/rates?active=true
```

---

## 3. Transacciones Core

### Depósito
```http
POST {{base_url}}/api/me/transactions/deposits
```

**Body:**
```json
{
  "targetAccountId": "abc-123",
  "amount": 100.00,
  "currency": "BOB",
  "method": "CASHBOX",
  "description": "Depósito inicial",
  "idempotencyKey": "dep-001-{{$timestamp}}"
}
```

### Transferencia interna
```http
POST {{base_url}}/api/me/transactions/transfers
```

**Body:**
```json
{
  "sourceAccountId": "abc-123",
  "targetAccountId": "def-456",
  "amount": 50.00,
  "currency": "BOB",
  "description": "Transferencia a cuenta de ahorros",
  "idempotencyKey": "trx-001-{{$timestamp}}"
}
```

### Pago
```http
POST {{base_url}}/api/me/transactions/payments
```

**Body:**
```json
{
  "sourceAccountId": "abc-123",
  "amount": 25.50,
  "currency": "BOB",
  "method": "QR",
  "externalReference": "ORDER-12345",
  "description": "Pago en comercio XYZ",
  "idempotencyKey": "pay-001-{{$timestamp}}"
}
```

### Retiro
```http
POST {{base_url}}/api/me/transactions/withdrawals
```

**Body:**
```json
{
  "sourceAccountId": "abc-123",
  "amount": 30.00,
  "currency": "BOB",
  "method": "CASHBOX",
  "description": "Retiro en ventanilla",
  "idempotencyKey": "wd-001-{{$timestamp}}"
}
```

### Hold (retener saldo)
```http
POST {{base_url}}/api/transactions/holds
```

**Body:**
```json
{
  "accountId": "abc-123",
  "amount": 100.00,
  "currency": "BOB",
  "description": "Retención por garantía",
  "idempotencyKey": "hold-001-{{$timestamp}}"
}
```

### Release (liberar saldo retenido)
```http
POST {{base_url}}/api/transactions/releases
```

**Body:**
```json
{
  "accountId": "abc-123",
  "amount": 100.00,
  "currency": "BOB",
  "description": "Liberación de garantía",
  "idempotencyKey": "rel-001-{{$timestamp}}"
}
```

---

## 4. Flujo QR Completo

### Paso 1: Crear intento QR (comercio/receptor)
```http
POST {{base_url}}/api/transactions/qr/intents
```

**Body:**
```json
{
  "targetAccountId": "def-456",
  "amount": 75.00,
  "currency": "BOB",
  "externalReference": "QR-STORE-001",
  "description": "Café La Paz - Mesa 5",
  "idempotencyKey": "qr-001-{{$timestamp}}"
}
```

**Respuesta (guardar el `id`):**
```json
{
  "id": "qr-intent-id-123",
  "status": "PENDING",
  "amount": 75.00,
  "currency": "BOB",
  "targetAccountId": "def-456",
  "createdAt": "2026-05-17T10:00:00Z"
}
```

### Paso 2: Confirmar QR (pagador/cliente)
```http
POST {{base_url}}/api/me/transactions/qr/{intentId}/confirm
```

**Body:**
```json
{
  "sourceAccountId": "abc-123",
  "idempotencyKey": "qr-confirm-001-{{$timestamp}}"
}
```

---

## 5. Reversos y Reembolsos

### Reversar una transacción (reversión completa)
```http
POST {{base_url}}/api/transactions/{transactionId}/reversal
```

**Body:**
```json
{
  "reason": "Error en el monto procesado",
  "idempotencyKey": "rev-001-{{$timestamp}}"
}
```

### Reembolsar una transacción
```http
POST {{base_url}}/api/transactions/{transactionId}/refunds
```

**Body:**
```json
{
  "amount": 50.00,
  "reason": "Producto devuelto por cliente",
  "idempotencyKey": "ref-001-{{$timestamp}}"
}
```

---

## 6. Consultas

### Historial de transacciones
```http
GET {{base_url}}/api/me/transactions?limit=20
```

### Detalle de una transacción específica
```http
GET {{base_url}}/api/me/transactions/{transactionId}
```

### Transacciones por cuenta
```http
GET {{base_url}}/api/accounts/{accountId}/transactions?limit=20
```

---

## 7. Límites (Limits)

### Crear regla de límite (admin)
```http
POST {{base_url}}/api/limits/rules
```

**Body:**
```json
{
  "code": "daily-transfer-limit",
  "name": "Límite diario de transferencias",
  "description": "Máximo 500 USD por día en transferencias",
  "limitType": "DAILY_AMOUNT",
  "scopeType": "TRANSACTION_TYPE",
  "period": "DAILY",
  "transactionType": "TRANSFER",
  "currency": "USD",
  "maxAmount": 500.00,
  "active": true,
  "requireReviewExceed": false
}
```

### Evaluar límites (útil para frontend pre-validación)
```http
POST {{base_url}}/api/limits/evaluate
```

**Body:**
```json
{
  "actorUserId": "user-123",
  "sourceAccountId": "abc-123",
  "sourceAccountType": "WALLET",
  "transactionType": "TRANSFER",
  "currency": "USD",
  "amount": 600.00
}
```

### Consultar uso actual de límites
```http
GET {{base_url}}/api/limits/usage?userId=user-123&transactionType=TRANSFER&currency=USD
```

---

## 8. Notificaciones

### Listar notificaciones
```http
GET {{base_url}}/api/me/notifications?limit=10
```

### Contador de no leídas
```http
GET {{base_url}}/api/me/notifications/unread-count
```

### Marcar como leída
```http
PATCH {{base_url}}/api/me/notifications/{notificationId}/read
```

**Body:** vacío

### Registrar dispositivo para push
```http
POST {{base_url}}/api/me/notification-devices
```

**Body:**
```json
{
  "deviceId": "device-unique-id",
  "fcmToken": "fcm-token-from-firebase",
  "platform": "ANDROID",
  "deviceName": "Xiaomi Note 12",
  "appVersion": "1.0.0",
  "osVersion": "13"
}
```

---

## 9. Accounting (Contabilidad)

### Listar periodos contables
```http
GET {{base_url}}/api/accounting/periods
```

### Cerrar periodo contable
```http
PATCH {{base_url}}/api/accounting/periods/{periodId}/close
```

**Body:**
```json
{
  "reason": "Cierre mensual mayo 2026"
}
```

### Listar asientos contables de una transacción
```http
GET {{base_url}}/api/accounting/journal-entries?transactionId={transactionId}
```

---

## 10. Governance (Auditoría)

### Listar eventos de auditoría
```http
GET {{base_url}}/api/audit-events?limit=20
```

### Filtrar por entidad
```http
GET {{base_url}}/api/audit-events?entityType=ACCOUNT&entityId={accountId}
```

---

## 🚀 Colección de Prueba en Orden

Este es el flujo que debes probar secuencialmente:

```
1. POST /api/me/accounts (crear cuenta origen)
2. POST /api/me/accounts (crear cuenta destino)
3. GET /api/me/accounts (verificar cuentas)
4. POST /api/fx/rates (admin - configurar tasa)
5. POST /api/fx/fees (admin - configurar comisión)
6. POST /api/me/transactions/deposits (depositar 1000 BOB)
7. GET /api/me/accounts/{id}/balance (verificar saldo: 1000)
8. POST /api/me/transactions/transfers (transferir 100 BOB)
9. GET /api/me/transactions (ver historial)
10. POST /api/transactions/qr/intents (crear QR de 50 BOB)
11. POST /api/me/transactions/qr/{id}/confirm (pagar QR)
12. GET /api/me/notifications (ver notificación de transferencia)
13. POST /api/transactions/{id}/reversal (reversar si hay error)
```

---

## 📝 Variables útiles para Postman

Crea estas variables de entorno en Postman para reutilizar IDs:

```javascript
// Después de crear cuenta
pm.environment.set("accountId", responseJson.id);

// Después de depósito
pm.environment.set("transactionId", responseJson.id);

// Después de QR intent
pm.environment.set("qrIntentId", responseJson.id);
```

**Ejemplo de prueba automatizada con tests:**

```javascript
// Test para depósito
pm.test("Depósito exitoso", function () {
    pm.response.to.have.status(200);
    pm.response.to.have.jsonBody("status", "COMPLETED");
    pm.response.to.have.jsonBody("type", "DEPOSIT");
});

pm.test("Saldo actualizado", function () {
    let balance = pm.response.json().targetAccount.availableBalance;
    pm.expect(balance).to.equal(1000.00);
});
```

¿Necesitas que profundice en algún endpoint específico o que te ayude a crear la colección completa para importar a Postman?


heldBalance = saldo retenido
hold = retener

/release = /liberar (liberar saldo detenido)