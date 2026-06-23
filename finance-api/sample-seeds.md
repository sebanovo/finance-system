# Sample Seeds

Enable the demo bundle with:

```env
APP_BOOTSTRAP_SAMPLE_DATA_ENABLED=true
```

The bootstrap is idempotent and reuses the existing platform and tenant bootstrap flow.

## Quick Summary

| Item | Total |
|---|---:|
| Demo tenants | 10 |
| Owner users | 10 |
| `USER` users | 10 |
| Tenant accounts | 30 |
| Transactions | 20 |
| Accounting periods | 20 |
| Journal entries | 10 |
| Limit rules | 20 |
| FX rates | 40 |
| Operation fees | 40 |
| Tenant notifications | 40 |
| Tenant notification preferences | 20 |
| Tenant audit events | 30 |
| Platform audit events | 20 |
| Tenant service enrollments | 10 |
| Public service customers | 10 |
| Public service bills | 20 |
| Public service payments | 10 |

## Demo Tenants

| Tenant name | Tenant slug | Plan | Owner email | USER email | Account prefix | Service provider code | Service customer code | Service customer name | Service alias | Owner wallet start | Savings start | Transfer amount | Pending bill | Paid bill |
|---|---|---|---|---|---|---|---|---|---|---:|---:|---:|---:|---:|
| Acme Finance | `acme-finance` | `DEMO` | `ariana@gmail.com` | `bruno@gmail.com` | `ACME` | `ELECTRICITY_CRE` | `12345678` | Casa Central | Luz del hogar | 1200.00 | 420.00 | 150.00 | 39.00 | 28.00 |
| Nova Wallet | `nova-wallet` | `BASIC` | `carla@gmail.com` | `elena@gmail.com` | `NOVA` | `INTERNET_ENTEL` | `NOVA-778899` | Internet Hogar | Internet principal | 980.00 | 305.00 | 150.00 | 42.00 | 28.00 |
| Pampa Pay | `pampa-pay` | `DEMO` | `lucia@gmail.com` | `felipe@gmail.com` | `PAMPA` | `WATER_SAGUAPAC` | `PAMPA-1001` | Casa Centro | Agua familiar | 1500.00 | 260.00 | 150.00 | 44.00 | 28.00 |
| Tumi Bank | `tumi-bank` | `BASIC` | `jose@gmail.com` | `gabriela@gmail.com` | `TUMI` | `TV_TIGO` | `TUMI-2222` | Hogar Norte | TV cable | 2100.00 | 510.00 | 150.00 | 45.00 | 28.00 |
| Luna Cash | `luna-cash` | `DEMO` | `maria@gmail.com` | `hector@gmail.com` | `LUNA` | `ELECTRICITY_CRE` | `LUNA-7788` | Residencial Sur | Energia luz | 800.00 | 190.00 | 150.00 | 45.00 | 27.00 |
| Andes Wallet | `andes-wallet` | `BASIC` | `pedro@gmail.com` | `irene@gmail.com` | `ANDES` | `INTERNET_ENTEL` | `ANDES-9900` | Oficina Central | Internet oficina | 1750.00 | 380.00 | 150.00 | 42.00 | 27.00 |
| Suma Finance | `suma-finance` | `DEMO` | `sofia@gmail.com` | `javier@gmail.com` | `SUMA` | `WATER_SAGUAPAC` | `SUMA-4455` | Barrio Este | Servicio agua | 1325.00 | 275.00 | 150.00 | 44.00 | 28.00 |
| Kawi Pay | `kawi-pay` | `BASIC` | `diego@gmail.com` | `karen@gmail.com` | `KAWI` | `TV_TIGO` | `KAWI-3003` | Ciudad Jardin | Tv familiar | 990.00 | 215.00 | 150.00 | 44.00 | 26.00 |
| Runa Cash | `runa-cash` | `DEMO` | `ana@gmail.com` | `luis@gmail.com` | `RUNA` | `ELECTRICITY_CRE` | `RUNA-5566` | Centro | Luz casa | 2450.00 | 610.00 | 150.00 | 43.00 | 27.00 |
| Pacha Wallet | `pacha-wallet` | `BASIC` | `camila@gmail.com` | `marta@gmail.com` | `PACHA` | `INTERNET_ENTEL` | `PACHA-9090` | Zona Norte | Internet hogar | 1110.00 | 330.00 | 150.00 | 44.00 | 26.00 |

### Derived account numbers

| Tenant slug | Wallet account | Savings account | Checking account |
|---|---|---|---|
| `acme-finance` | `ACME-WAL-BOB-000001` | `ACME-SAV-BOB-000001` | `ACME-CHK-BOB-000001` |
| `nova-wallet` | `NOVA-WAL-BOB-000001` | `NOVA-SAV-BOB-000001` | `NOVA-CHK-BOB-000001` |
| `pampa-pay` | `PAMPA-WAL-BOB-000001` | `PAMPA-SAV-BOB-000001` | `PAMPA-CHK-BOB-000001` |
| `tumi-bank` | `TUMI-WAL-BOB-000001` | `TUMI-SAV-BOB-000001` | `TUMI-CHK-BOB-000001` |
| `luna-cash` | `LUNA-WAL-BOB-000001` | `LUNA-SAV-BOB-000001` | `LUNA-CHK-BOB-000001` |
| `andes-wallet` | `ANDES-WAL-BOB-000001` | `ANDES-SAV-BOB-000001` | `ANDES-CHK-BOB-000001` |
| `suma-finance` | `SUMA-WAL-BOB-000001` | `SUMA-SAV-BOB-000001` | `SUMA-CHK-BOB-000001` |
| `kawi-pay` | `KAWI-WAL-BOB-000001` | `KAWI-SAV-BOB-000001` | `KAWI-CHK-BOB-000001` |
| `runa-cash` | `RUNA-WAL-BOB-000001` | `RUNA-SAV-BOB-000001` | `RUNA-CHK-BOB-000001` |
| `pacha-wallet` | `PACHA-WAL-BOB-000001` | `PACHA-SAV-BOB-000001` | `PACHA-CHK-BOB-000001` |

## Plan Limits

| Plan | Max users | Max roles | Trial days | Seeded tenants |
|---|---:|---:|---:|---|
| `DEMO` | 5 | 3 | 10 | `acme-finance`, `pampa-pay`, `luna-cash`, `suma-finance`, `runa-cash` |
| `BASIC` | 10 | 5 | - | `nova-wallet`, `tumi-bank`, `andes-wallet`, `kawi-pay`, `pacha-wallet` |
| `PRO` | 25 | 10 | - | Not used by the demo bundle |
| `ENTERPRISE` | 9999 | 999 | - | Not used by the demo bundle |

### Coherence rules

| Rule | Value |
|---|---|
| Users per tenant | 2 total: 1 `OWNER_ADMIN` and 1 `USER` |
| Accounts per tenant | 3 total: 1 `WALLET`, 1 `SAVINGS`, 1 `CHECKING` |
| Transfers per tenant | 2 transfers, each for `150.00 BOB` |
| Demo tenant compliance | 2 users stays under `DEMO.maxUsers = 5` |
| Basic tenant compliance | 2 users stays under `BASIC.maxUsers = 10` |
| Role compliance | The seeded role count stays within each plan `maxRoles` |

## Tenant Bundle Structure

| Item | Value |
|---|---|
| Owner role | `OWNER_ADMIN` |
| Customer role | `USER` |
| Currency | `BOB` |
| Primary account type | `WALLET` |
| Secondary account type | `SAVINGS` |
| Customer account type | `CHECKING` |
| Owner wallet movement | `DEBIT` / `CREDIT` on the wallet and savings pair |
| USER movement | `DEBIT` from owner wallet and `CREDIT` to customer checking |
| Service enrollment | 1 active enrollment for the tenant owner |

## Common Transfer and Balance Model

| Concept | Value |
|---|---|
| Transfer amount | `150.00 BOB` |
| Wallet final balance | `owner wallet start - 300.00` |
| Savings final balance | `savings start + 150.00` |
| Checking final balance | `150.00` |
| Wallet account number format | `{PREFIX}-WAL-BOB-000001` |
| Savings account number format | `{PREFIX}-SAV-BOB-000001` |
| Checking account number format | `{PREFIX}-CHK-BOB-000001` |

## Service Provider Catalog

| Code | Name | Category | Customer code label | Used by tenant |
|---|---|---|---|---|
| `ELECTRICITY_CRE` | `CRE` | `ELECTRICITY` | `Codigo de suministro` | `acme-finance`, `luna-cash`, `runa-cash` |
| `WATER_SAGUAPAC` | `SAGUAPAC` | `WATER` | `Codigo de suministro` | `pampa-pay`, `suma-finance` |
| `INTERNET_ENTEL` | `Entel Internet` | `INTERNET` | `Codigo de cliente` | `nova-wallet`, `andes-wallet`, `pacha-wallet` |
| `TV_TIGO` | `Tigo TV` | `TV_CABLE` | `Numero de abonado` | `tumi-bank`, `kawi-pay` |

## Limit Rules

| Code suffix | Name | Limit type | Scope | Period | Transaction type | Currency | Max amount | Max count | Review on exceed |
|---|---|---|---|---|---|---|---:|---:|---|
| `DAILY_TRANSFER_AMOUNT` | Limite diario de transferencias | `DAILY_AMOUNT` | `TENANT` | `DAILY` | `TRANSFER` | `BOB` | 2500.00 | - | Yes |
| `MONTHLY_WITHDRAWAL_COUNT` | Limite mensual de retiros | `MONTHLY_COUNT` | `TENANT` | `MONTHLY` | `WITHDRAWAL` | - | - | 20 | Yes |

## FX Rates

| Source | Target | Rate | Description |
|---|---|---:|---|
| `BOB` | `USD` | 0.14500000 | BOB a USD |
| `USD` | `BOB` | 6.89000000 | USD a BOB |
| `BOB` | `EUR` | 0.13200000 | BOB a EUR |
| `EUR` | `BOB` | 7.42000000 | EUR a BOB |

## Operation Fees

| Operation code | Fee type | Fee value | Calculation mode | Description |
|---|---|---:|---|---|
| `TRANSFER` | `FIXED` | 1.50 | `SEPARATE` | Tarifa por transferencia |
| `WITHDRAWAL` | `PERCENTAGE` | 0.50 | `SEPARATE` | Tarifa por retiro |
| `PAYMENT` | `FIXED` | 0.80 | `INCLUDED` | Tarifa por pago |
| `SERVICE_PAYMENT` | `FIXED` | 0.75 | `SEPARATE` | Tarifa por pago de servicios |

`SERVICE_PAYMENT` is used by the service-payments dashboard and matches the backend enum.

## Notifications

The seed creates the same notification pattern for the owner user and the tenant `USER`.

| Recipient | Notification type | Category | Priority | Title | Body |
|---|---|---|---|---|---|
| Owner | `TRANSFER_SENT` | `TRANSACTIONS` | `HIGH` | `Transaccion demo completada` | `Se genero la transaccion demo {transactionId}` |
| Owner | `SYSTEM_NOTICE` | `ACCOUNTS` | `NORMAL` | `Resumen de cuentas demo` | `Tus cuentas demo ya estan listas` |
| USER | `TRANSFER_SENT` | `TRANSACTIONS` | `HIGH` | `Transaccion demo completada` | `Se genero la transaccion demo {transactionId}` |
| USER | `SYSTEM_NOTICE` | `ACCOUNTS` | `NORMAL` | `Resumen de cuentas demo` | `Tus cuentas demo ya estan listas` |

## Audit Events

### Platform audit events

| Event type | Resource type | Resource id | Details |
|---|---|---|---|
| `TENANT_BOOTSTRAPPED` | `TENANT` | Tenant UUID | Tenant demo created and seeded |
| `SUBSCRIPTION_ASSIGNED` | `SUBSCRIPTION` | Plan code | Current subscription assigned for demo tenant |

### Tenant audit events

| Event type | Resource type | Resource id | Details |
|---|---|---|---|
| `LOGIN_SUCCESS` | `AUTH` | Owner user UUID | Owner login demo |
| `TRANSACTION_CREATED` | `TRANSACTIONS` | Owner transfer transaction UUID | Demo transfer created |
| `TRANSFER_RECEIVED` | `TRANSACTIONS` | USER transfer transaction UUID | USER demo received a transfer |

## Public Service Data

| Seeded row | Value |
|---|---|
| Public service customer | 1 per tenant |
| Pending bill | 1 per tenant, current UTC billing period |
| Paid bill | 1 per tenant, previous UTC billing period |
| Payment receipt | 1 per tenant, linked to the paid bill |
| Pending amount formula | `39 + abs(slug.hashCode() % 7)` |
| Paid amount formula | `24 + abs(slug.hashCode() % 5)` |

### Service bill amounts by tenant

| Tenant slug | Pending bill | Paid bill |
|---|---:|---:|
| `acme-finance` | 39.00 | 28.00 |
| `nova-wallet` | 42.00 | 28.00 |
| `pampa-pay` | 44.00 | 28.00 |
| `tumi-bank` | 45.00 | 28.00 |
| `luna-cash` | 45.00 | 27.00 |
| `andes-wallet` | 42.00 | 27.00 |
| `suma-finance` | 44.00 | 28.00 |
| `kawi-pay` | 44.00 | 26.00 |
| `runa-cash` | 43.00 | 27.00 |
| `pacha-wallet` | 44.00 | 26.00 |

## User Credentials

All demo passwords are:

```text
password
```

| Tenant slug | Owner email | USER email |
|---|---|---|
| `acme-finance` | `ariana@gmail.com` | `bruno@gmail.com` |
| `nova-wallet` | `carla@gmail.com` | `elena@gmail.com` |
| `pampa-pay` | `lucia@gmail.com` | `felipe@gmail.com` |
| `tumi-bank` | `jose@gmail.com` | `gabriela@gmail.com` |
| `luna-cash` | `maria@gmail.com` | `hector@gmail.com` |
| `andes-wallet` | `pedro@gmail.com` | `irene@gmail.com` |
| `suma-finance` | `sofia@gmail.com` | `javier@gmail.com` |
| `kawi-pay` | `diego@gmail.com` | `karen@gmail.com` |
| `runa-cash` | `ana@gmail.com` | `luis@gmail.com` |
| `pacha-wallet` | `camila@gmail.com` | `marta@gmail.com` |

## Notes

| Note | Value |
|---|---|
| Seed mode | Optional, controlled by `APP_BOOTSTRAP_SAMPLE_DATA_ENABLED` |
| Idempotency | Deterministic IDs and upserts keep reruns safe |
| Scope | The sample bundle does not replace the normal platform bootstrap |
| Dashboards | The data is designed to keep tenant, platform, reporting, and service-payments dashboards populated |
