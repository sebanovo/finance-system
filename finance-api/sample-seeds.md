# Sample Seeds

Enable the demo bundle with:

```env
APP_BOOTSTRAP_SAMPLE_DATA_ENABLED=true
```

The bootstrap is idempotent and reuses the existing platform and tenant bootstrap flow.

## Executive Summary

All seeded tenants use the `ENTERPRISE` plan so the dataset stays coherent with:

- 10 users per tenant
- 2 accounts per user
- 10 transactions per user
- mixed service bills: first 5 users pending, last 4 users paid

| Item | Total |
|---|---:|
| Demo tenants | 10 |
| Tenant users | 100 |
| Owner users | 10 |
| `USER` users | 90 |
| Tenant accounts | 200 |
| Tenant transactions | 1000 |
| Tenant loans | 800 |
| Tenant loan installments | 800 |
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
| Public service bills | 710 |
| Public service payments | 500 |
