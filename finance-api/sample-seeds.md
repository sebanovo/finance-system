# Sample Seeds

Enable the demo bundle with:

```env
APP_BOOTSTRAP_SAMPLE_DATA_ENABLED=true
```

The bootstrap is idempotent and reuses the existing platform and tenant bootstrap flow.

## Executive Summary

All seeded tenants now use the `ENTERPRISE` plan so the dataset can stay coherent with:

- 10 users per tenant
- 2 accounts per user
- 10 transactions per user
- 10 service payments per user

| Item | Total |
|---|---:|
| Demo tenants | 10 |
| Tenant users | 100 |
| Owner users | 10 |
| `USER` users | 90 |
| Tenant accounts | 200 |
| Tenant transactions | 1000 |
| Accounting periods | 20 |
| Journal entries | 10 |
| Limit rules | 20 |
| FX rates | 40 |
| Operation fees | 40 |
| Tenant notifications | 200 |
| Tenant notification preferences | 100 |
| Tenant audit events | 110 |
| Platform audit events | 20 |
| Tenant service enrollments | 100 |
| Public service customers | 100 |
| Public service bills | 1010 |
| Public service payments | 1000 |

## Demo Tenants

| Tenant name | Tenant slug | Plan | Account prefix | Owner email | First user email | Service provider code | Service customer code | Service customer name | Service alias |
|---|---|---|---|---|---|---|---|---|---|
| Acme Finance | `acme-finance` | `ENTERPRISE` | `ACME` | `ariana@gmail.com` | `bruno@gmail.com` | `ELECTRICITY_CRE` | `12345678` | Casa Central | Luz del hogar |
| Nova Wallet | `nova-wallet` | `ENTERPRISE` | `NOVA` | `carla@gmail.com` | `elena@gmail.com` | `INTERNET_ENTEL` | `NOVA-778899` | Internet Hogar | Internet principal |
| Pampa Pay | `pampa-pay` | `ENTERPRISE` | `PAMPA` | `lucia@gmail.com` | `felipe@gmail.com` | `WATER_SAGUAPAC` | `PAMPA-1001` | Casa Centro | Agua familiar |
| Tumi Bank | `tumi-bank` | `ENTERPRISE` | `TUMI` | `jose@gmail.com` | `gabriela@gmail.com` | `TV_TIGO` | `TUMI-2222` | Hogar Norte | TV cable |
| Luna Cash | `luna-cash` | `ENTERPRISE` | `LUNA` | `maria@gmail.com` | `hector@gmail.com` | `ELECTRICITY_CRE` | `LUNA-7788` | Residencial Sur | Energia luz |
| Andes Wallet | `andes-wallet` | `ENTERPRISE` | `ANDES` | `pedro@gmail.com` | `irene@gmail.com` | `INTERNET_ENTEL` | `ANDES-9900` | Oficina Central | Internet oficina |
| Suma Finance | `suma-finance` | `ENTERPRISE` | `SUMA` | `sofia@gmail.com` | `javier@gmail.com` | `WATER_SAGUAPAC` | `SUMA-4455` | Barrio Este | Servicio agua |
| Kawi Pay | `kawi-pay` | `ENTERPRISE` | `KAWI` | `diego@gmail.com` | `karen@gmail.com` | `TV_TIGO` | `KAWI-3003` | Ciudad Jardin | Tv familiar |
| Runa Cash | `runa-cash` | `ENTERPRISE` | `RUNA` | `ana@gmail.com` | `luis@gmail.com` | `ELECTRICITY_CRE` | `RUNA-5566` | Centro | Luz casa |
| Pacha Wallet | `pacha-wallet` | `ENTERPRISE` | `PACHA` | `camila@gmail.com` | `marta@gmail.com` | `INTERNET_ENTEL` | `PACHA-9090` | Zona Norte | Internet hogar |

## Plan Limits

The sample bundle only uses `ENTERPRISE`, but the platform still defines the other plans.

| Plan | Max users | Max roles | Trial days | Used by sample bundle |
|---|---:|---:|---:|---|
| `DEMO` | 5 | 3 | 10 | No |
| `BASIC` | 10 | 5 | - | No |
| `PRO` | 25 | 10 | - | No |
| `ENTERPRISE` | 9999 | 999 | - | Yes |

### Coherence rules

| Rule | Value |
|---|---|
| Users per tenant | 10 total: 1 `OWNER_ADMIN` + 9 `USER` |
| Accounts per tenant | 20 total: 2 per user |
| Transactions per tenant | 100 total: 10 per user |
| Tenant compliance | 10 users stays well under `ENTERPRISE.maxUsers = 9999` |
| Role compliance | The seeded role count stays within `ENTERPRISE.maxRoles` |

## User Roster

| Tenant slug | Slot | Role | Email | Name | Password |
|---|---|---|---|---|---|
| `acme-finance` | Owner | `OWNER_ADMIN` | `ariana@gmail.com` | Ariana Lopez | `password` |
| `acme-finance` | User 01 | `USER` | `bruno@gmail.com` | Bruno Diaz | `password` |
| `acme-finance` | User 02 | `USER` | `acmefinance02@gmail.com` | User 02 Demo | `password` |
| `acme-finance` | User 03 | `USER` | `acmefinance03@gmail.com` | User 03 Demo | `password` |
| `acme-finance` | User 04 | `USER` | `acmefinance04@gmail.com` | User 04 Demo | `password` |
| `acme-finance` | User 05 | `USER` | `acmefinance05@gmail.com` | User 05 Demo | `password` |
| `acme-finance` | User 06 | `USER` | `acmefinance06@gmail.com` | User 06 Demo | `password` |
| `acme-finance` | User 07 | `USER` | `acmefinance07@gmail.com` | User 07 Demo | `password` |
| `acme-finance` | User 08 | `USER` | `acmefinance08@gmail.com` | User 08 Demo | `password` |
| `acme-finance` | User 09 | `USER` | `acmefinance09@gmail.com` | User 09 Demo | `password` |
| `nova-wallet` | Owner | `OWNER_ADMIN` | `carla@gmail.com` | Carla Perez | `password` |
| `nova-wallet` | User 01 | `USER` | `elena@gmail.com` | Elena Gutierrez | `password` |
| `nova-wallet` | User 02 | `USER` | `novawallet02@gmail.com` | User 02 Demo | `password` |
| `nova-wallet` | User 03 | `USER` | `novawallet03@gmail.com` | User 03 Demo | `password` |
| `nova-wallet` | User 04 | `USER` | `novawallet04@gmail.com` | User 04 Demo | `password` |
| `nova-wallet` | User 05 | `USER` | `novawallet05@gmail.com` | User 05 Demo | `password` |
| `nova-wallet` | User 06 | `USER` | `novawallet06@gmail.com` | User 06 Demo | `password` |
| `nova-wallet` | User 07 | `USER` | `novawallet07@gmail.com` | User 07 Demo | `password` |
| `nova-wallet` | User 08 | `USER` | `novawallet08@gmail.com` | User 08 Demo | `password` |
| `nova-wallet` | User 09 | `USER` | `novawallet09@gmail.com` | User 09 Demo | `password` |
| `pampa-pay` | Owner | `OWNER_ADMIN` | `lucia@gmail.com` | Lucia Mendoza | `password` |
| `pampa-pay` | User 01 | `USER` | `felipe@gmail.com` | Felipe Rojas | `password` |
| `pampa-pay` | User 02 | `USER` | `pampapay02@gmail.com` | User 02 Demo | `password` |
| `pampa-pay` | User 03 | `USER` | `pampapay03@gmail.com` | User 03 Demo | `password` |
| `pampa-pay` | User 04 | `USER` | `pampapay04@gmail.com` | User 04 Demo | `password` |
| `pampa-pay` | User 05 | `USER` | `pampapay05@gmail.com` | User 05 Demo | `password` |
| `pampa-pay` | User 06 | `USER` | `pampapay06@gmail.com` | User 06 Demo | `password` |
| `pampa-pay` | User 07 | `USER` | `pampapay07@gmail.com` | User 07 Demo | `password` |
| `pampa-pay` | User 08 | `USER` | `pampapay08@gmail.com` | User 08 Demo | `password` |
| `pampa-pay` | User 09 | `USER` | `pampapay09@gmail.com` | User 09 Demo | `password` |
| `tumi-bank` | Owner | `OWNER_ADMIN` | `jose@gmail.com` | Jose Vargas | `password` |
| `tumi-bank` | User 01 | `USER` | `gabriela@gmail.com` | Gabriela Sosa | `password` |
| `tumi-bank` | User 02 | `USER` | `tumibank02@gmail.com` | User 02 Demo | `password` |
| `tumi-bank` | User 03 | `USER` | `tumibank03@gmail.com` | User 03 Demo | `password` |
| `tumi-bank` | User 04 | `USER` | `tumibank04@gmail.com` | User 04 Demo | `password` |
| `tumi-bank` | User 05 | `USER` | `tumibank05@gmail.com` | User 05 Demo | `password` |
| `tumi-bank` | User 06 | `USER` | `tumibank06@gmail.com` | User 06 Demo | `password` |
| `tumi-bank` | User 07 | `USER` | `tumibank07@gmail.com` | User 07 Demo | `password` |
| `tumi-bank` | User 08 | `USER` | `tumibank08@gmail.com` | User 08 Demo | `password` |
| `tumi-bank` | User 09 | `USER` | `tumibank09@gmail.com` | User 09 Demo | `password` |
| `luna-cash` | Owner | `OWNER_ADMIN` | `maria@gmail.com` | Maria Fernandez | `password` |
| `luna-cash` | User 01 | `USER` | `hector@gmail.com` | Hector Lima | `password` |
| `luna-cash` | User 02 | `USER` | `lunacash02@gmail.com` | User 02 Demo | `password` |
| `luna-cash` | User 03 | `USER` | `lunacash03@gmail.com` | User 03 Demo | `password` |
| `luna-cash` | User 04 | `USER` | `lunacash04@gmail.com` | User 04 Demo | `password` |
| `luna-cash` | User 05 | `USER` | `lunacash05@gmail.com` | User 05 Demo | `password` |
| `luna-cash` | User 06 | `USER` | `lunacash06@gmail.com` | User 06 Demo | `password` |
| `luna-cash` | User 07 | `USER` | `lunacash07@gmail.com` | User 07 Demo | `password` |
| `luna-cash` | User 08 | `USER` | `lunacash08@gmail.com` | User 08 Demo | `password` |
| `luna-cash` | User 09 | `USER` | `lunacash09@gmail.com` | User 09 Demo | `password` |
| `andes-wallet` | Owner | `OWNER_ADMIN` | `pedro@gmail.com` | Pedro Quispe | `password` |
| `andes-wallet` | User 01 | `USER` | `irene@gmail.com` | Irene Valdez | `password` |
| `andes-wallet` | User 02 | `USER` | `andeswallet02@gmail.com` | User 02 Demo | `password` |
| `andes-wallet` | User 03 | `USER` | `andeswallet03@gmail.com` | User 03 Demo | `password` |
| `andes-wallet` | User 04 | `USER` | `andeswallet04@gmail.com` | User 04 Demo | `password` |
| `andes-wallet` | User 05 | `USER` | `andeswallet05@gmail.com` | User 05 Demo | `password` |
| `andes-wallet` | User 06 | `USER` | `andeswallet06@gmail.com` | User 06 Demo | `password` |
| `andes-wallet` | User 07 | `USER` | `andeswallet07@gmail.com` | User 07 Demo | `password` |
| `andes-wallet` | User 08 | `USER` | `andeswallet08@gmail.com` | User 08 Demo | `password` |
| `andes-wallet` | User 09 | `USER` | `andeswallet09@gmail.com` | User 09 Demo | `password` |
| `suma-finance` | Owner | `OWNER_ADMIN` | `sofia@gmail.com` | Sofia Rojas | `password` |
| `suma-finance` | User 01 | `USER` | `javier@gmail.com` | Javier Mora | `password` |
| `suma-finance` | User 02 | `USER` | `sumafinance02@gmail.com` | User 02 Demo | `password` |
| `suma-finance` | User 03 | `USER` | `sumafinance03@gmail.com` | User 03 Demo | `password` |
| `suma-finance` | User 04 | `USER` | `sumafinance04@gmail.com` | User 04 Demo | `password` |
| `suma-finance` | User 05 | `USER` | `sumafinance05@gmail.com` | User 05 Demo | `password` |
| `suma-finance` | User 06 | `USER` | `sumafinance06@gmail.com` | User 06 Demo | `password` |
| `suma-finance` | User 07 | `USER` | `sumafinance07@gmail.com` | User 07 Demo | `password` |
| `suma-finance` | User 08 | `USER` | `sumafinance08@gmail.com` | User 08 Demo | `password` |
| `suma-finance` | User 09 | `USER` | `sumafinance09@gmail.com` | User 09 Demo | `password` |
| `kawi-pay` | Owner | `OWNER_ADMIN` | `diego@gmail.com` | Diego Salazar | `password` |
| `kawi-pay` | User 01 | `USER` | `karen@gmail.com` | Karen Nunez | `password` |
| `kawi-pay` | User 02 | `USER` | `kawipay02@gmail.com` | User 02 Demo | `password` |
| `kawi-pay` | User 03 | `USER` | `kawipay03@gmail.com` | User 03 Demo | `password` |
| `kawi-pay` | User 04 | `USER` | `kawipay04@gmail.com` | User 04 Demo | `password` |
| `kawi-pay` | User 05 | `USER` | `kawipay05@gmail.com` | User 05 Demo | `password` |
| `kawi-pay` | User 06 | `USER` | `kawipay06@gmail.com` | User 06 Demo | `password` |
| `kawi-pay` | User 07 | `USER` | `kawipay07@gmail.com` | User 07 Demo | `password` |
| `kawi-pay` | User 08 | `USER` | `kawipay08@gmail.com` | User 08 Demo | `password` |
| `kawi-pay` | User 09 | `USER` | `kawipay09@gmail.com` | User 09 Demo | `password` |
| `runa-cash` | Owner | `OWNER_ADMIN` | `ana@gmail.com` | Ana Torrez | `password` |
| `runa-cash` | User 01 | `USER` | `luis@gmail.com` | Luis Paredes | `password` |
| `runa-cash` | User 02 | `USER` | `runacash02@gmail.com` | User 02 Demo | `password` |
| `runa-cash` | User 03 | `USER` | `runacash03@gmail.com` | User 03 Demo | `password` |
| `runa-cash` | User 04 | `USER` | `runacash04@gmail.com` | User 04 Demo | `password` |
| `runa-cash` | User 05 | `USER` | `runacash05@gmail.com` | User 05 Demo | `password` |
| `runa-cash` | User 06 | `USER` | `runacash06@gmail.com` | User 06 Demo | `password` |
| `runa-cash` | User 07 | `USER` | `runacash07@gmail.com` | User 07 Demo | `password` |
| `runa-cash` | User 08 | `USER` | `runacash08@gmail.com` | User 08 Demo | `password` |
| `runa-cash` | User 09 | `USER` | `runacash09@gmail.com` | User 09 Demo | `password` |
| `pacha-wallet` | Owner | `OWNER_ADMIN` | `camila@gmail.com` | Camila Flores | `password` |
| `pacha-wallet` | User 01 | `USER` | `marta@gmail.com` | Marta Campos | `password` |
| `pacha-wallet` | User 02 | `USER` | `pachawallet02@gmail.com` | User 02 Demo | `password` |
| `pacha-wallet` | User 03 | `USER` | `pachawallet03@gmail.com` | User 03 Demo | `password` |
| `pacha-wallet` | User 04 | `USER` | `pachawallet04@gmail.com` | User 04 Demo | `password` |
| `pacha-wallet` | User 05 | `USER` | `pachawallet05@gmail.com` | User 05 Demo | `password` |
| `pacha-wallet` | User 06 | `USER` | `pachawallet06@gmail.com` | User 06 Demo | `password` |
| `pacha-wallet` | User 07 | `USER` | `pachawallet07@gmail.com` | User 07 Demo | `password` |
| `pacha-wallet` | User 08 | `USER` | `pachawallet08@gmail.com` | User 08 Demo | `password` |
| `pacha-wallet` | User 09 | `USER` | `pachawallet09@gmail.com` | User 09 Demo | `password` |

## Account Pattern

All tenants use the same account numbering rules, only the tenant prefix changes.

| Slot | Wallet account | Secondary account |
|---|---|---|
| Owner | `{PREFIX}-WAL-BOB-000001` | `{PREFIX}-SAV-BOB-000001` |
| User 01 | `{PREFIX}-WAL-BOB-000002` | `{PREFIX}-CHK-BOB-000001` |
| User 02 | `{PREFIX}-WAL-BOB-000003` | `{PREFIX}-CHK-BOB-000002` |
| User 03 | `{PREFIX}-WAL-BOB-000004` | `{PREFIX}-CHK-BOB-000003` |
| User 04 | `{PREFIX}-WAL-BOB-000005` | `{PREFIX}-CHK-BOB-000004` |
| User 05 | `{PREFIX}-WAL-BOB-000006` | `{PREFIX}-CHK-BOB-000005` |
| User 06 | `{PREFIX}-WAL-BOB-000007` | `{PREFIX}-CHK-BOB-000006` |
| User 07 | `{PREFIX}-WAL-BOB-000008` | `{PREFIX}-CHK-BOB-000007` |
| User 08 | `{PREFIX}-WAL-BOB-000009` | `{PREFIX}-CHK-BOB-000008` |
| User 09 | `{PREFIX}-WAL-BOB-000010` | `{PREFIX}-CHK-BOB-000009` |

## Transaction Pattern

Each user gets 10 alternating transfers between their two accounts.

| Slot | Transaction key prefix | Amount | Direction |
|---|---|---:|---|
| Owner | `owner-transfer-1..10` | 150.00 BOB | Wallet <-> Savings |
| User 01 | `user-01-transfer-1..10` | 150.00 BOB | Wallet <-> Checking |
| User 02 | `user-02-transfer-1..10` | 150.00 BOB | Wallet <-> Checking |
| User 03 | `user-03-transfer-1..10` | 150.00 BOB | Wallet <-> Checking |
| User 04 | `user-04-transfer-1..10` | 150.00 BOB | Wallet <-> Checking |
| User 05 | `user-05-transfer-1..10` | 150.00 BOB | Wallet <-> Checking |
| User 06 | `user-06-transfer-1..10` | 150.00 BOB | Wallet <-> Checking |
| User 07 | `user-07-transfer-1..10` | 150.00 BOB | Wallet <-> Checking |
| User 08 | `user-08-transfer-1..10` | 150.00 BOB | Wallet <-> Checking |
| User 09 | `user-09-transfer-1..10` | 150.00 BOB | Wallet <-> Checking |

## Service Provider Catalog

| Code | Name | Category | Customer code label |
|---|---|---|---|
| `ELECTRICITY_CRE` | `CRE` | `ELECTRICITY` | `Codigo de suministro` |
| `WATER_SAGUAPAC` | `SAGUAPAC` | `WATER` | `Codigo de suministro` |
| `INTERNET_ENTEL` | `Entel Internet` | `INTERNET` | `Codigo de cliente` |
| `TV_TIGO` | `Tigo TV` | `TV_CABLE` | `Numero de abonado` |

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

Each tenant gets the same notification shape for the owner and for each of the 9 `USER` accounts.

| Recipient | Notification type | Category | Priority | Title | Body |
|---|---|---|---|---|---|
| Owner | `TRANSFER_SENT` | `TRANSACTIONS` | `HIGH` | Transaccion demo completada | Se genero la transaccion demo {transactionId} |
| Owner | `SYSTEM_NOTICE` | `ACCOUNTS` | `NORMAL` | Resumen de cuentas demo | Tus cuentas demo ya estan listas |
| USER | `TRANSFER_SENT` | `TRANSACTIONS` | `HIGH` | Transaccion demo completada | Se genero la transaccion demo {transactionId} |
| USER | `SYSTEM_NOTICE` | `ACCOUNTS` | `NORMAL` | Resumen de cuentas demo | Tus cuentas demo ya estan listas |

## Audit Events

### Platform audit events

| Event type | Resource type | Resource id | Details |
|---|---|---|---|
| `TENANT_BOOTSTRAPPED` | `TENANT` | Tenant UUID | Tenant demo created and seeded |
| `SUBSCRIPTION_ASSIGNED` | `SUBSCRIPTION` | `ENTERPRISE` | Current subscription assigned for demo tenant |

### Tenant audit events

| Event type | Resource type | Resource id | Details |
|---|---|---|---|
| `LOGIN_SUCCESS` | `AUTH` | Owner user UUID | Owner login demo |
| `TRANSACTION_CREATED` | `TRANSACTIONS` | Owner last transfer UUID | Demo transfer series created |
| `TRANSFER_RECEIVED` | `TRANSACTIONS` | USER last transfer UUID | USER demo received a transfer series |

## Public Service Data

| Tenant slug | Service customers | Paid bills/payments | Current pending bill | Provider code |
|---|---|---:|---:|---|
| `acme-finance` | 10 customers (`12345678`, `12345678-U01` .. `12345678-U09`) | 100 | 1 | `ELECTRICITY_CRE` |
| `nova-wallet` | 10 customers (`NOVA-778899`, `NOVA-778899-U01` .. `U09`) | 100 | 1 | `INTERNET_ENTEL` |
| `pampa-pay` | 10 customers (`PAMPA-1001`, `PAMPA-1001-U01` .. `U09`) | 100 | 1 | `WATER_SAGUAPAC` |
| `tumi-bank` | 10 customers (`TUMI-2222`, `TUMI-2222-U01` .. `U09`) | 100 | 1 | `TV_TIGO` |
| `luna-cash` | 10 customers (`LUNA-7788`, `LUNA-7788-U01` .. `U09`) | 100 | 1 | `ELECTRICITY_CRE` |
| `andes-wallet` | 10 customers (`ANDES-9900`, `ANDES-9900-U01` .. `U09`) | 100 | 1 | `INTERNET_ENTEL` |
| `suma-finance` | 10 customers (`SUMA-4455`, `SUMA-4455-U01` .. `U09`) | 100 | 1 | `WATER_SAGUAPAC` |
| `kawi-pay` | 10 customers (`KAWI-3003`, `KAWI-3003-U01` .. `U09`) | 100 | 1 | `TV_TIGO` |
| `runa-cash` | 10 customers (`RUNA-5566`, `RUNA-5566-U01` .. `U09`) | 100 | 1 | `ELECTRICITY_CRE` |
| `pacha-wallet` | 10 customers (`PACHA-9090`, `PACHA-9090-U01` .. `U09`) | 100 | 1 | `INTERNET_ENTEL` |

Each customer gets 10 paid bills and 10 matching payments across the previous 10 billing periods. The owner keeps one current `PENDING` bill to keep the dashboard populated.

## Notes

| Note | Value |
|---|---|
| Seed mode | Optional, controlled by `APP_BOOTSTRAP_SAMPLE_DATA_ENABLED` |
| Passwords | All demo passwords are `password` |
| Idempotency | Deterministic IDs and upserts keep reruns safe |
| Scope | The sample bundle does not replace the normal platform bootstrap |
| Dashboards | The data is designed to keep tenant, platform, reporting, and service-payments dashboards populated |
