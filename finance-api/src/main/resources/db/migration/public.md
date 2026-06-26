# Directory Export: /home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/resources/db/migration/public

_Generated on 2026-06-25 04:38:44Z_

## Summary

- Source directory: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/resources/db/migration/public`
- Output file: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/resources/db/migration/public.md`

## Files

### `V10__create_report_executions.sql`

```sql
-- =====================================================================
-- Reporting module (Fase 2) — audited executions + export metadata.
--
-- Both tables live in `public` with a denormalized snapshot of the actor
-- (no cross-schema FKs between public and tenant_<slug>). result_json is
-- capped at the application layer (REPORT_SNAPSHOT_MAX_ROWS / _BYTES).
-- Export files live on disk (storage_path); only metadata is stored here.
-- =====================================================================

CREATE TABLE IF NOT EXISTS public.report_executions (
    id                         UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Snapshot of the actor (no FK to tenant schemas).
    requested_by_user_id       UUID,
    requested_by_email         VARCHAR(255),
    requested_by_display_name  VARCHAR(255),
    actor_scope                VARCHAR(20)  NOT NULL,   -- PLATFORM | TENANT
    tenant_slug                VARCHAR(100),            -- null when global
    tenant_schema              VARCHAR(128),

    kind                       VARCHAR(20)  NOT NULL,   -- CONTROLLED | AI
    definition_key             VARCHAR(120),            -- controlled reports
    prompt                     TEXT,                    -- AI reports (sensitive)
    transcript                 TEXT,                    -- AI voice (sensitive)

    sql                        TEXT,
    referenced_views           JSONB,
    schema_used                VARCHAR(128),
    result_json                JSONB,                   -- capped snapshot
    row_count                  INTEGER,
    truncated                  BOOLEAN NOT NULL DEFAULT FALSE,

    status                     VARCHAR(20)  NOT NULL,   -- PENDING | SUCCESS | FAILED
    error_message              TEXT,

    created_at                 TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    executed_at                TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_report_executions_created_at
    ON public.report_executions (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_report_executions_status
    ON public.report_executions (status);
CREATE INDEX IF NOT EXISTS idx_report_executions_tenant_slug
    ON public.report_executions (tenant_slug);
CREATE INDEX IF NOT EXISTS idx_report_executions_requested_by
    ON public.report_executions (requested_by_user_id);

CREATE TABLE IF NOT EXISTS public.report_exports (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    execution_id       UUID NOT NULL REFERENCES public.report_executions (id) ON DELETE CASCADE,
    format             VARCHAR(10)  NOT NULL,           -- PDF | XLSX
    mode               VARCHAR(20)  NOT NULL,           -- SNAPSHOT | FULL
    file_name          VARCHAR(255) NOT NULL,
    content_type       VARCHAR(120) NOT NULL,
    file_size_bytes    BIGINT,
    storage_path       VARCHAR(500) NOT NULL,           -- on disk, not bytea
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by_user_id UUID
);

CREATE INDEX IF NOT EXISTS idx_report_exports_execution_id
    ON public.report_exports (execution_id);

```

### `V11__extend_reporting_regenerate_views.sql`

```sql
-- =====================================================================
-- Reporting catalog batch 2 (Fase 6) — extend reporting.regenerate_views()
-- with two more cross-tenant views:
--   reporting.tenant_users               (users per tenant)
--   reporting.tenant_financial_activity  (movements per tenant + status)
--
-- Full CREATE OR REPLACE of the function (keeps platform_overview and
-- tenant_movement_ranking from V9). Same safety: quote_ident via format(),
-- pg_advisory_xact_lock, skips schemas missing a source view.
-- =====================================================================

CREATE OR REPLACE FUNCTION reporting.regenerate_views()
RETURNS void
LANGUAGE plpgsql
AS $func$
DECLARE
    v_schema          text;
    v_slug            text;
    v_overview_parts  text[] := ARRAY[]::text[];
    v_ranking_parts   text[] := ARRAY[]::text[];
    v_users_parts     text[] := ARRAY[]::text[];
    v_financial_parts text[] := ARRAY[]::text[];
    v_sql             text;
BEGIN
    PERFORM pg_advisory_xact_lock(918273);

    FOR v_schema IN
        SELECT schema_name
        FROM information_schema.schemata
        WHERE schema_name LIKE 'tenant\_%'
        ORDER BY schema_name
    LOOP
        v_slug := substring(v_schema FROM 8);

        IF EXISTS (SELECT 1 FROM information_schema.views
                   WHERE table_schema = v_schema AND table_name = 'reporting_tenant_executive_overview') THEN
            v_overview_parts := v_overview_parts || format(
                'SELECT %L::text AS tenant_slug, o.* FROM %I.reporting_tenant_executive_overview o',
                v_slug, v_schema);
        END IF;

        IF EXISTS (SELECT 1 FROM information_schema.views
                   WHERE table_schema = v_schema AND table_name = 'reporting_tenant_movements') THEN
            v_ranking_parts := v_ranking_parts || format(
                'SELECT %L::text AS tenant_slug, COUNT(*)::bigint AS transaction_count, '
                || 'COALESCE(SUM(m.amount), 0)::numeric AS total_amount, MAX(m.created_at) AS last_transaction_at '
                || 'FROM %I.reporting_tenant_movements m',
                v_slug, v_schema);

            v_financial_parts := v_financial_parts || format(
                'SELECT %L::text AS tenant_slug, m.status AS status, COUNT(*)::bigint AS transaction_count, '
                || 'COALESCE(SUM(m.amount), 0)::numeric AS total_amount '
                || 'FROM %I.reporting_tenant_movements m GROUP BY m.status',
                v_slug, v_schema);
        END IF;

        IF EXISTS (SELECT 1 FROM information_schema.views
                   WHERE table_schema = v_schema AND table_name = 'reporting_tenant_users_activity') THEN
            v_users_parts := v_users_parts || format(
                'SELECT %L::text AS tenant_slug, COUNT(*)::bigint AS total_users, '
                || 'COUNT(*) FILTER (WHERE u.active)::bigint AS active_users '
                || 'FROM %I.reporting_tenant_users_activity u',
                v_slug, v_schema);
        END IF;
    END LOOP;

    -- platform_overview
    IF array_length(v_overview_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::bigint AS active_users, '
              || 'NULL::bigint AS active_accounts, NULL::numeric AS total_available_balance, '
              || 'NULL::numeric AS total_held_balance, NULL::bigint AS completed_transactions, '
              || 'NULL::numeric AS completed_volume WHERE false';
    ELSE
        v_sql := array_to_string(v_overview_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.platform_overview';
    EXECUTE 'CREATE VIEW reporting.platform_overview AS ' || v_sql;

    -- tenant_movement_ranking
    IF array_length(v_ranking_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::bigint AS transaction_count, '
              || 'NULL::numeric AS total_amount, NULL::timestamptz AS last_transaction_at WHERE false';
    ELSE
        v_sql := array_to_string(v_ranking_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.tenant_movement_ranking';
    EXECUTE 'CREATE VIEW reporting.tenant_movement_ranking AS ' || v_sql;

    -- tenant_users
    IF array_length(v_users_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::bigint AS total_users, '
              || 'NULL::bigint AS active_users WHERE false';
    ELSE
        v_sql := array_to_string(v_users_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.tenant_users';
    EXECUTE 'CREATE VIEW reporting.tenant_users AS ' || v_sql;

    -- tenant_financial_activity
    IF array_length(v_financial_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::text AS status, '
              || 'NULL::bigint AS transaction_count, NULL::numeric AS total_amount WHERE false';
    ELSE
        v_sql := array_to_string(v_financial_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.tenant_financial_activity';
    EXECUTE 'CREATE VIEW reporting.tenant_financial_activity AS ' || v_sql;

    GRANT SELECT ON reporting.platform_overview            TO finance_platform_readonly;
    GRANT SELECT ON reporting.tenant_movement_ranking      TO finance_platform_readonly;
    GRANT SELECT ON reporting.tenant_users                 TO finance_platform_readonly;
    GRANT SELECT ON reporting.tenant_financial_activity    TO finance_platform_readonly;
END;
$func$;

-- Rebuild now with the extended definition.
SELECT reporting.regenerate_views();

```

### `V12__create_reporting_platform_views.sql`

```sql
-- =====================================================================
-- Reporting catalog batch 3 (Fase 6) — platform views that read public tables
-- directly (NOT cross-tenant UNIONs), so they are static views in `reporting`,
-- independent of reporting.regenerate_views().
--
-- Sensitive/free-text columns are omitted (ip_address, user_agent,
-- event_details, before/after state, storage_path).
-- =====================================================================

-- Platform audit (SuperAdmin).
CREATE OR REPLACE VIEW reporting.platform_audit AS
SELECT
    e.id            AS audit_id,
    e.created_at    AS created_at,
    e.tenant_slug   AS tenant_slug,
    e.actor_email   AS actor_email,
    e.event_type    AS event_type,
    e.resource_type AS resource_type,
    e.resource_id   AS resource_id,
    e.source        AS source,
    e.outcome       AS outcome
FROM public.platform_audit_events e;

-- Backups and restorations.
CREATE OR REPLACE VIEW reporting.platform_backups AS
SELECT
    b.id             AS backup_id,
    b.created_at     AS created_at,
    b.operation_type AS operation_type,
    b.scope          AS scope,
    b.status         AS status,
    b.tenant_slug    AS tenant_slug,
    b.schema_name    AS schema_name,
    b.file_name      AS file_name,
    b.format         AS format,
    b.size_bytes     AS size_bytes,
    b.requested_by   AS requested_by,
    b.failure_reason AS failure_reason,
    b.started_at     AS started_at,
    b.finished_at    AS finished_at
FROM public.backup_jobs b;

-- Subscriptions and plans.
CREATE OR REPLACE VIEW reporting.platform_subscriptions AS
SELECT
    s.id                   AS subscription_id,
    t.slug                 AS tenant_slug,
    t.name                 AS tenant_name,
    p.code                 AS plan_code,
    p.name                 AS plan_name,
    s.status               AS status,
    s.is_trial             AS is_trial,
    s.current_subscription AS current_subscription,
    s.started_at           AS started_at,
    s.expires_at           AS expires_at,
    s.created_at           AS created_at
FROM public.platform_subscriptions s
JOIN public.platform_tenants t ON t.id = s.tenant_id
JOIN public.platform_plans p ON p.id = s.plan_id;

GRANT SELECT ON reporting.platform_audit         TO finance_platform_readonly;
GRANT SELECT ON reporting.platform_backups       TO finance_platform_readonly;
GRANT SELECT ON reporting.platform_subscriptions TO finance_platform_readonly;

```

### `V13__extend_reporting_operational_risk.sql`

```sql
-- =====================================================================
-- Reporting catalog batch 4 (Fase 6) — add reporting.tenant_operational_risk
-- (failed/reversed transactions + blocked accounts per tenant).
-- Full CREATE OR REPLACE of regenerate_views() keeping all prior views.
-- =====================================================================

CREATE OR REPLACE FUNCTION reporting.regenerate_views()
RETURNS void
LANGUAGE plpgsql
AS $func$
DECLARE
    v_schema          text;
    v_slug            text;
    v_overview_parts  text[] := ARRAY[]::text[];
    v_ranking_parts   text[] := ARRAY[]::text[];
    v_users_parts     text[] := ARRAY[]::text[];
    v_financial_parts text[] := ARRAY[]::text[];
    v_risk_parts      text[] := ARRAY[]::text[];
    v_sql             text;
BEGIN
    PERFORM pg_advisory_xact_lock(918273);

    FOR v_schema IN
        SELECT schema_name FROM information_schema.schemata
        WHERE schema_name LIKE 'tenant\_%'
        ORDER BY schema_name
    LOOP
        v_slug := substring(v_schema FROM 8);

        IF EXISTS (SELECT 1 FROM information_schema.views
                   WHERE table_schema = v_schema AND table_name = 'reporting_tenant_executive_overview') THEN
            v_overview_parts := v_overview_parts || format(
                'SELECT %L::text AS tenant_slug, o.* FROM %I.reporting_tenant_executive_overview o',
                v_slug, v_schema);
        END IF;

        IF EXISTS (SELECT 1 FROM information_schema.views
                   WHERE table_schema = v_schema AND table_name = 'reporting_tenant_movements') THEN
            v_ranking_parts := v_ranking_parts || format(
                'SELECT %L::text AS tenant_slug, COUNT(*)::bigint AS transaction_count, '
                || 'COALESCE(SUM(m.amount), 0)::numeric AS total_amount, MAX(m.created_at) AS last_transaction_at '
                || 'FROM %I.reporting_tenant_movements m',
                v_slug, v_schema);

            v_financial_parts := v_financial_parts || format(
                'SELECT %L::text AS tenant_slug, m.status AS status, COUNT(*)::bigint AS transaction_count, '
                || 'COALESCE(SUM(m.amount), 0)::numeric AS total_amount '
                || 'FROM %I.reporting_tenant_movements m GROUP BY m.status',
                v_slug, v_schema);

            v_risk_parts := v_risk_parts || format(
                'SELECT %L::text AS tenant_slug, '
                || 'COUNT(*) FILTER (WHERE m.status = ''FAILED'')::bigint AS failed_transactions, '
                || 'COUNT(*) FILTER (WHERE m.is_reversal)::bigint AS reversals, '
                || '(SELECT COUNT(*) FROM %I.reporting_tenant_accounts_balances a WHERE a.status = ''BLOCKED'')::bigint AS blocked_accounts '
                || 'FROM %I.reporting_tenant_movements m',
                v_slug, v_schema, v_schema);
        END IF;

        IF EXISTS (SELECT 1 FROM information_schema.views
                   WHERE table_schema = v_schema AND table_name = 'reporting_tenant_users_activity') THEN
            v_users_parts := v_users_parts || format(
                'SELECT %L::text AS tenant_slug, COUNT(*)::bigint AS total_users, '
                || 'COUNT(*) FILTER (WHERE u.active)::bigint AS active_users '
                || 'FROM %I.reporting_tenant_users_activity u',
                v_slug, v_schema);
        END IF;
    END LOOP;

    -- platform_overview
    IF array_length(v_overview_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::bigint AS active_users, '
              || 'NULL::bigint AS active_accounts, NULL::numeric AS total_available_balance, '
              || 'NULL::numeric AS total_held_balance, NULL::bigint AS completed_transactions, '
              || 'NULL::numeric AS completed_volume WHERE false';
    ELSE
        v_sql := array_to_string(v_overview_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.platform_overview';
    EXECUTE 'CREATE VIEW reporting.platform_overview AS ' || v_sql;

    -- tenant_movement_ranking
    IF array_length(v_ranking_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::bigint AS transaction_count, '
              || 'NULL::numeric AS total_amount, NULL::timestamptz AS last_transaction_at WHERE false';
    ELSE
        v_sql := array_to_string(v_ranking_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.tenant_movement_ranking';
    EXECUTE 'CREATE VIEW reporting.tenant_movement_ranking AS ' || v_sql;

    -- tenant_users
    IF array_length(v_users_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::bigint AS total_users, '
              || 'NULL::bigint AS active_users WHERE false';
    ELSE
        v_sql := array_to_string(v_users_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.tenant_users';
    EXECUTE 'CREATE VIEW reporting.tenant_users AS ' || v_sql;

    -- tenant_financial_activity
    IF array_length(v_financial_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::text AS status, '
              || 'NULL::bigint AS transaction_count, NULL::numeric AS total_amount WHERE false';
    ELSE
        v_sql := array_to_string(v_financial_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.tenant_financial_activity';
    EXECUTE 'CREATE VIEW reporting.tenant_financial_activity AS ' || v_sql;

    -- tenant_operational_risk
    IF array_length(v_risk_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::bigint AS failed_transactions, '
              || 'NULL::bigint AS reversals, NULL::bigint AS blocked_accounts WHERE false';
    ELSE
        v_sql := array_to_string(v_risk_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.tenant_operational_risk';
    EXECUTE 'CREATE VIEW reporting.tenant_operational_risk AS ' || v_sql;

    GRANT SELECT ON reporting.platform_overview         TO finance_platform_readonly;
    GRANT SELECT ON reporting.tenant_movement_ranking   TO finance_platform_readonly;
    GRANT SELECT ON reporting.tenant_users              TO finance_platform_readonly;
    GRANT SELECT ON reporting.tenant_financial_activity TO finance_platform_readonly;
    GRANT SELECT ON reporting.tenant_operational_risk   TO finance_platform_readonly;
END;
$func$;

SELECT reporting.regenerate_views();

```

### `V14__add_report_executions_request_params.sql`

```sql
-- =====================================================================
-- Add request_params to report_executions (controlled-report filters, used to
-- rerun controlled reports). Added as a separate migration because V10 was
-- already applied; never edit an applied migration.
-- =====================================================================

ALTER TABLE public.report_executions
    ADD COLUMN IF NOT EXISTS request_params JSONB;

```

### `V15__create_public_service_payments.sql`

```sql
CREATE TABLE IF NOT EXISTS public.service_providers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(80) NOT NULL,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(40) NOT NULL,
    service_customer_code_label VARCHAR(100) NOT NULL DEFAULT 'Código de cliente',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_service_providers_code UNIQUE (code),
    CONSTRAINT chk_service_provider_code_uppercase CHECK (code = upper(btrim(code))),
    CONSTRAINT chk_service_provider_category CHECK (category IN (
        'ELECTRICITY',
        'WATER',
        'INTERNET',
        'TV_CABLE'
    )),
    CONSTRAINT chk_service_provider_status CHECK (status IN (
        'ACTIVE',
        'INACTIVE'
    ))
);

CREATE INDEX IF NOT EXISTS idx_service_providers_category
    ON public.service_providers(category);

CREATE INDEX IF NOT EXISTS idx_service_providers_status
    ON public.service_providers(status);

CREATE TABLE IF NOT EXISTS public.service_customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id UUID NOT NULL REFERENCES public.service_providers(id),
    service_customer_code VARCHAR(100) NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_service_customer_provider_code UNIQUE (provider_id, service_customer_code),
    CONSTRAINT chk_service_customer_status CHECK (status IN (
        'ACTIVE',
        'INACTIVE'
    ))
);

CREATE INDEX IF NOT EXISTS idx_service_customers_provider_id
    ON public.service_customers(provider_id);

CREATE INDEX IF NOT EXISTS idx_service_customers_service_customer_code
    ON public.service_customers(service_customer_code);

CREATE INDEX IF NOT EXISTS idx_service_customers_status
    ON public.service_customers(status);

CREATE TABLE IF NOT EXISTS public.service_bills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id UUID NOT NULL REFERENCES public.service_providers(id),
    service_customer_id UUID NOT NULL REFERENCES public.service_customers(id),
    service_customer_code VARCHAR(100) NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    billing_period VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'BOB',
    due_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    paid_by_tenant_id UUID NULL REFERENCES public.platform_tenants(id),
    paid_by_tenant_slug VARCHAR(100) NULL,
    paid_by_user_id UUID NULL,
    paid_by_account_id UUID NULL,
    paid_by_account_number VARCHAR(50) NULL,
    paid_transaction_id UUID NULL,
    paid_at TIMESTAMPTZ NULL,
    created_by_superadmin_id UUID NULL REFERENCES public.platform_superadmins(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_service_bill_provider_customer_period UNIQUE (provider_id, service_customer_code, billing_period),
    CONSTRAINT chk_service_bill_amount CHECK (amount > 0),
    CONSTRAINT chk_service_bill_status CHECK (status IN (
        'PENDING',
        'PAID',
        'EXPIRED',
        'CANCELLED',
        'REVERSED'
    ))
);

CREATE INDEX IF NOT EXISTS idx_service_bills_provider_id
    ON public.service_bills(provider_id);

CREATE INDEX IF NOT EXISTS idx_service_bills_service_customer_code
    ON public.service_bills(service_customer_code);

CREATE INDEX IF NOT EXISTS idx_service_bills_status
    ON public.service_bills(status);

CREATE INDEX IF NOT EXISTS idx_service_bills_billing_period
    ON public.service_bills(billing_period);

CREATE INDEX IF NOT EXISTS idx_service_bills_due_date
    ON public.service_bills(due_date);

CREATE INDEX IF NOT EXISTS idx_service_bills_paid_by_tenant_slug
    ON public.service_bills(paid_by_tenant_slug);

CREATE INDEX IF NOT EXISTS idx_service_bills_paid_transaction_id
    ON public.service_bills(paid_transaction_id);

CREATE TABLE IF NOT EXISTS public.service_bill_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID NOT NULL REFERENCES public.service_bills(id),
    provider_id UUID NOT NULL REFERENCES public.service_providers(id),
    paid_by_tenant_id UUID NOT NULL REFERENCES public.platform_tenants(id),
    paid_by_tenant_slug VARCHAR(100) NOT NULL,
    paid_by_user_id UUID NOT NULL,
    paid_by_account_id UUID NOT NULL,
    paid_by_account_number VARCHAR(50) NOT NULL,
    paid_transaction_id UUID NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    receipt_number VARCHAR(100) NOT NULL,
    idempotency_key VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PAID',
    paid_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_service_bill_payment_bill UNIQUE (bill_id),
    CONSTRAINT uq_service_bill_payment_receipt_number UNIQUE (receipt_number),
    CONSTRAINT uq_service_bill_payment_idempotency UNIQUE (paid_by_tenant_id, paid_by_user_id, idempotency_key),
    CONSTRAINT chk_service_bill_payment_status CHECK (status IN (
        'PAID',
        'REVERSED'
    )),
    CONSTRAINT chk_service_bill_payment_amount CHECK (amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_provider_id
    ON public.service_bill_payments(provider_id);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_paid_by_tenant_slug
    ON public.service_bill_payments(paid_by_tenant_slug);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_paid_by_user_id
    ON public.service_bill_payments(paid_by_user_id);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_paid_by_account_number
    ON public.service_bill_payments(paid_by_account_number);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_paid_transaction_id
    ON public.service_bill_payments(paid_transaction_id);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_receipt_number
    ON public.service_bill_payments(receipt_number);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_paid_at
    ON public.service_bill_payments(paid_at);

INSERT INTO public.service_providers (
    code,
    name,
    category,
    service_customer_code_label,
    status,
    created_at,
    updated_at
)
VALUES
    ('ELECTRICITY_CRE', 'CRE', 'ELECTRICITY', 'Código de suministro', 'ACTIVE', NOW(), NOW()),
    ('WATER_SAGUAPAC', 'SAGUAPAC', 'WATER', 'Código de suministro', 'ACTIVE', NOW(), NOW()),
    ('INTERNET_ENTEL', 'Entel Internet', 'INTERNET', 'Código de cliente', 'ACTIVE', NOW(), NOW()),
    ('TV_TIGO', 'Tigo TV', 'TV_CABLE', 'Número de abonado', 'ACTIVE', NOW(), NOW())
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    category = EXCLUDED.category,
    service_customer_code_label = EXCLUDED.service_customer_code_label,
    status = 'ACTIVE',
    updated_at = NOW();

```

### `V16__create_service_payment_receipt_seq.sql`

```sql
CREATE SEQUENCE IF NOT EXISTS public.service_payment_receipt_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

```

### `V17__drop_backup_jobs_tenant_fk.sql`

```sql
ALTER TABLE public.backup_jobs
    DROP CONSTRAINT IF EXISTS backup_jobs_tenant_id_fkey;

```

### `V1__init_public.sql`

```sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
```

### `V2__create_platform_plans.sql`

```sql
CREATE TABLE IF NOT EXISTS public.platform_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),

    max_users INTEGER NOT NULL DEFAULT 10,
    max_roles INTEGER NOT NULL DEFAULT 5,

    plan_type VARCHAR(20) NOT NULL DEFAULT 'PAID',
    trial_days INTEGER NULL,

    monthly_amount NUMERIC(19, 4) NULL,
    yearly_amount NUMERIC(19, 4) NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',

    stripe_product_id VARCHAR(120) NULL,
    stripe_monthly_price_id VARCHAR(120) NULL,
    stripe_yearly_price_id VARCHAR(120) NULL,

    public_visible BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INTEGER NOT NULL DEFAULT 0,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_platform_plans_plan_type CHECK (
        plan_type IN ('DEMO', 'PAID', 'ENTERPRISE')
    ),

    CONSTRAINT chk_platform_plans_trial_days CHECK (
        trial_days IS NULL OR trial_days >= 0
    ),

    CONSTRAINT chk_platform_plans_monthly_amount CHECK (
        monthly_amount IS NULL OR monthly_amount >= 0
    ),

    CONSTRAINT chk_platform_plans_yearly_amount CHECK (
        yearly_amount IS NULL OR yearly_amount >= 0
    )
);

CREATE INDEX IF NOT EXISTS idx_platform_plans_code
    ON public.platform_plans(code);

CREATE INDEX IF NOT EXISTS idx_platform_plans_plan_type
    ON public.platform_plans(plan_type);

CREATE INDEX IF NOT EXISTS idx_platform_plans_active
    ON public.platform_plans(active);

CREATE INDEX IF NOT EXISTS idx_platform_plans_public_visible
    ON public.platform_plans(public_visible);

CREATE INDEX IF NOT EXISTS idx_platform_plans_sort_order
    ON public.platform_plans(sort_order);

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_plans_stripe_product_id
    ON public.platform_plans(stripe_product_id)
    WHERE stripe_product_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_plans_stripe_monthly_price_id
    ON public.platform_plans(stripe_monthly_price_id)
    WHERE stripe_monthly_price_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_plans_stripe_yearly_price_id
    ON public.platform_plans(stripe_yearly_price_id)
    WHERE stripe_yearly_price_id IS NOT NULL;

```

### `V3__create_system_permissions.sql`

```sql
CREATE TABLE IF NOT EXISTS public.system_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL UNIQUE,
    module VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### `V4__create_platform_tenants.sql`

```sql
CREATE TABLE IF NOT EXISTS public.platform_tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    schema_name VARCHAR(128) NOT NULL UNIQUE,

    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    plan_id UUID NULL REFERENCES public.platform_plans(id),

    stripe_customer_id VARCHAR(120) NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_platform_tenants_slug
    ON public.platform_tenants(slug);

CREATE INDEX IF NOT EXISTS idx_platform_tenants_schema_name
    ON public.platform_tenants(schema_name);

CREATE INDEX IF NOT EXISTS idx_platform_tenants_status
    ON public.platform_tenants(status);

CREATE INDEX IF NOT EXISTS idx_platform_tenants_plan_id
    ON public.platform_tenants(plan_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_tenants_stripe_customer_id
    ON public.platform_tenants(stripe_customer_id)
    WHERE stripe_customer_id IS NOT NULL;

```

### `V5__create_platform_superadmins.sql`

```sql
CREATE TABLE IF NOT EXISTS public.platform_superadmins (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_platform_superadmins_email
    ON public.platform_superadmins(email);
```

### `V6__create_platform_audit_events.sql`

```sql
CREATE TABLE IF NOT EXISTS public.platform_audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_subject VARCHAR(150),
    actor_id UUID,
    actor_email VARCHAR(255),
    tenant_slug VARCHAR(100),
    event_type VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100),
    resource_id VARCHAR(100),
    event_details TEXT,
    ip_address INET,
    user_agent TEXT,
    request_id VARCHAR(100),
    correlation_id VARCHAR(100),
    source VARCHAR(50) NOT NULL DEFAULT 'APPLICATION',
    outcome VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    before_state JSONB,
    after_state JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_platform_audit_events_created_at
    ON public.platform_audit_events(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_platform_audit_events_event_type
    ON public.platform_audit_events(event_type);

CREATE INDEX IF NOT EXISTS idx_platform_audit_events_actor_id
    ON public.platform_audit_events(actor_id);

CREATE INDEX IF NOT EXISTS idx_platform_audit_events_tenant_slug
    ON public.platform_audit_events(tenant_slug);

CREATE INDEX IF NOT EXISTS idx_platform_audit_events_request_id
    ON public.platform_audit_events(request_id);

```

### `V7__enhance_platform_plans_and_create_platform_suscriptions.sql`

```sql
ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS plan_type VARCHAR(20) NOT NULL DEFAULT 'PAID';

ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS trial_days INTEGER NULL;

ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS monthly_amount NUMERIC(19, 4) NULL;

ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS yearly_amount NUMERIC(19, 4) NULL;

ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS currency VARCHAR(10) NOT NULL DEFAULT 'USD';

ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS stripe_product_id VARCHAR(120) NULL;

ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS stripe_monthly_price_id VARCHAR(120) NULL;

ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS stripe_yearly_price_id VARCHAR(120) NULL;

ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS public_visible BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE public.platform_plans
    ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_platform_plans_public_visible
    ON public.platform_plans(public_visible);

CREATE INDEX IF NOT EXISTS idx_platform_plans_sort_order
    ON public.platform_plans(sort_order);

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_plans_stripe_product_id
    ON public.platform_plans(stripe_product_id)
    WHERE stripe_product_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_plans_stripe_monthly_price_id
    ON public.platform_plans(stripe_monthly_price_id)
    WHERE stripe_monthly_price_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_plans_stripe_yearly_price_id
    ON public.platform_plans(stripe_yearly_price_id)
    WHERE stripe_yearly_price_id IS NOT NULL;

ALTER TABLE public.platform_tenants
    ADD COLUMN IF NOT EXISTS stripe_customer_id VARCHAR(120) NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_tenants_stripe_customer_id
    ON public.platform_tenants(stripe_customer_id)
    WHERE stripe_customer_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS public.platform_subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL REFERENCES public.platform_tenants(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES public.platform_plans(id),

    status VARCHAR(30) NOT NULL,

    is_trial BOOLEAN NOT NULL DEFAULT FALSE,
    current_subscription BOOLEAN NOT NULL DEFAULT TRUE,

    stripe_subscription_id VARCHAR(120) NULL,
    stripe_price_id VARCHAR(120) NULL,

    billing_interval VARCHAR(20) NULL,

    started_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NULL,

    current_period_start TIMESTAMPTZ NULL,
    current_period_end TIMESTAMPTZ NULL,

    cancel_at_period_end BOOLEAN NOT NULL DEFAULT FALSE,
    cancelled_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_platform_subscriptions_billing_interval CHECK (
        billing_interval IS NULL OR billing_interval IN ('MONTHLY', 'YEARLY')
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_subscriptions_current_per_tenant
    ON public.platform_subscriptions(tenant_id)
    WHERE current_subscription = true;

CREATE UNIQUE INDEX IF NOT EXISTS uq_platform_subscriptions_stripe_subscription_id
    ON public.platform_subscriptions(stripe_subscription_id)
    WHERE stripe_subscription_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_platform_subscriptions_tenant_id
    ON public.platform_subscriptions(tenant_id);

CREATE INDEX IF NOT EXISTS idx_platform_subscriptions_plan_id
    ON public.platform_subscriptions(plan_id);

CREATE INDEX IF NOT EXISTS idx_platform_subscriptions_status
    ON public.platform_subscriptions(status);

CREATE INDEX IF NOT EXISTS idx_platform_subscriptions_stripe_price_id
    ON public.platform_subscriptions(stripe_price_id);

CREATE INDEX IF NOT EXISTS idx_platform_subscriptions_current_period_end
    ON public.platform_subscriptions(current_period_end);


CREATE TABLE IF NOT EXISTS public.subscription_checkout_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL REFERENCES public.platform_tenants(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES public.platform_plans(id),

    requested_by_user_id UUID NULL,
    requested_by_email VARCHAR(255) NULL,

    billing_interval VARCHAR(20) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    stripe_customer_id VARCHAR(120) NULL,
    stripe_session_id VARCHAR(120) NOT NULL,
    stripe_subscription_id VARCHAR(120) NULL,
    stripe_price_id VARCHAR(120) NULL,

    checkout_url TEXT NULL,

    success_url TEXT NULL,
    cancel_url TEXT NULL,

    amount NUMERIC(19, 4) NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',

    completed_at TIMESTAMPTZ NULL,
    expires_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_subscription_checkout_sessions_billing_interval CHECK (
        billing_interval IN ('MONTHLY', 'YEARLY')
    ),

    CONSTRAINT chk_subscription_checkout_sessions_status CHECK (
        status IN ('PENDING', 'OPEN', 'COMPLETED', 'EXPIRED', 'CANCELLED', 'FAILED')
    ),

    CONSTRAINT chk_subscription_checkout_sessions_amount CHECK (
        amount IS NULL OR amount >= 0
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_subscription_checkout_sessions_stripe_session_id
    ON public.subscription_checkout_sessions(stripe_session_id);

CREATE INDEX IF NOT EXISTS idx_subscription_checkout_sessions_tenant_id
    ON public.subscription_checkout_sessions(tenant_id);

CREATE INDEX IF NOT EXISTS idx_subscription_checkout_sessions_plan_id
    ON public.subscription_checkout_sessions(plan_id);

CREATE INDEX IF NOT EXISTS idx_subscription_checkout_sessions_status
    ON public.subscription_checkout_sessions(status);

CREATE INDEX IF NOT EXISTS idx_subscription_checkout_sessions_requested_by_user_id
    ON public.subscription_checkout_sessions(requested_by_user_id);

CREATE INDEX IF NOT EXISTS idx_subscription_checkout_sessions_created_at
    ON public.subscription_checkout_sessions(created_at DESC);


CREATE TABLE IF NOT EXISTS public.subscription_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL REFERENCES public.platform_tenants(id) ON DELETE CASCADE,
    platform_subscription_id UUID NULL REFERENCES public.platform_subscriptions(id) ON DELETE SET NULL,
    plan_id UUID NULL REFERENCES public.platform_plans(id) ON DELETE SET NULL,

    stripe_customer_id VARCHAR(120) NULL,
    stripe_subscription_id VARCHAR(120) NULL,
    stripe_invoice_id VARCHAR(120) NULL,
    stripe_payment_intent_id VARCHAR(120) NULL,
    stripe_charge_id VARCHAR(120) NULL,

    invoice_number VARCHAR(120) NULL,
    hosted_invoice_url TEXT NULL,
    invoice_pdf_url TEXT NULL,

    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL,

    status VARCHAR(30) NOT NULL,

    billing_reason VARCHAR(80) NULL,

    paid_at TIMESTAMPTZ NULL,
    failed_at TIMESTAMPTZ NULL,

    failure_reason TEXT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_subscription_payments_status CHECK (
        status IN ('PAID', 'FAILED', 'REFUNDED', 'PARTIALLY_REFUNDED', 'VOID')
    ),

    CONSTRAINT chk_subscription_payments_amount CHECK (
        amount >= 0
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_subscription_payments_stripe_invoice_id
    ON public.subscription_payments(stripe_invoice_id)
    WHERE stripe_invoice_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_subscription_payments_tenant_id
    ON public.subscription_payments(tenant_id);

CREATE INDEX IF NOT EXISTS idx_subscription_payments_platform_subscription_id
    ON public.subscription_payments(platform_subscription_id);

CREATE INDEX IF NOT EXISTS idx_subscription_payments_plan_id
    ON public.subscription_payments(plan_id);

CREATE INDEX IF NOT EXISTS idx_subscription_payments_status
    ON public.subscription_payments(status);

CREATE INDEX IF NOT EXISTS idx_subscription_payments_paid_at
    ON public.subscription_payments(paid_at DESC);

CREATE INDEX IF NOT EXISTS idx_subscription_payments_created_at
    ON public.subscription_payments(created_at DESC);


CREATE TABLE IF NOT EXISTS public.stripe_webhook_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    stripe_event_id VARCHAR(120) NOT NULL,
    event_type VARCHAR(120) NOT NULL,

    processed BOOLEAN NOT NULL DEFAULT FALSE,
    processed_at TIMESTAMPTZ NULL,

    processing_attempts INTEGER NOT NULL DEFAULT 0,

    last_error TEXT NULL,

    payload JSONB NULL,

    received_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_stripe_webhook_events_processing_attempts CHECK (
        processing_attempts >= 0
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_stripe_webhook_events_stripe_event_id
    ON public.stripe_webhook_events(stripe_event_id);

CREATE INDEX IF NOT EXISTS idx_stripe_webhook_events_event_type
    ON public.stripe_webhook_events(event_type);

CREATE INDEX IF NOT EXISTS idx_stripe_webhook_events_processed
    ON public.stripe_webhook_events(processed);

CREATE INDEX IF NOT EXISTS idx_stripe_webhook_events_received_at
    ON public.stripe_webhook_events(received_at DESC);

```

### `V8__create_backup_jobs.sql`

```sql
ALTER TABLE public.platform_tenants
    ADD COLUMN IF NOT EXISTS maintenance_mode BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS maintenance_reason TEXT NULL,
    ADD COLUMN IF NOT EXISTS maintenance_started_at TIMESTAMPTZ NULL;

CREATE TABLE IF NOT EXISTS public.backup_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    operation_type VARCHAR(30) NOT NULL,
    scope VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    tenant_id UUID NULL REFERENCES public.platform_tenants(id) ON DELETE SET NULL,
    tenant_slug VARCHAR(100) NULL,
    schema_name VARCHAR(128) NULL,
    source_backup_id UUID NULL REFERENCES public.backup_jobs(id) ON DELETE SET NULL,
    pre_restore_backup_id UUID NULL REFERENCES public.backup_jobs(id) ON DELETE SET NULL,
    file_name VARCHAR(255) NOT NULL,
    storage_path TEXT NOT NULL,
    format VARCHAR(30) NOT NULL DEFAULT 'CUSTOM',
    size_bytes BIGINT NULL,
    checksum_sha256 VARCHAR(128) NULL,
    requested_by VARCHAR(150),
    reason TEXT NULL,
    failure_reason TEXT NULL,
    started_at TIMESTAMPTZ NULL,
    finished_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_backup_jobs_operation_type CHECK (
        operation_type IN ('BACKUP', 'RESTORE')
    ),

    CONSTRAINT chk_backup_jobs_scope CHECK (
        scope IN ('TENANT_SCHEMA', 'FULL_DATABASE')
    ),

    CONSTRAINT chk_backup_jobs_status CHECK (
        status IN (
            'PENDING',
            'RUNNING',
            'COMPLETED',
            'FAILED',
            'RESTORING',
            'RESTORED',
            'RESTORED_WITH_WARNINGS',
            'RESTORE_FAILED'
        )
    ),

    CONSTRAINT chk_backup_jobs_format CHECK (
        format IN ('CUSTOM', 'SQL')
    )
);

CREATE INDEX IF NOT EXISTS idx_backup_jobs_tenant_slug
    ON public.backup_jobs(tenant_slug);

CREATE INDEX IF NOT EXISTS idx_backup_jobs_status
    ON public.backup_jobs(status);

CREATE INDEX IF NOT EXISTS idx_backup_jobs_created_at
    ON public.backup_jobs(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_backup_jobs_scope
    ON public.backup_jobs(scope);

CREATE UNIQUE INDEX IF NOT EXISTS uq_backup_jobs_active_per_tenant
    ON public.backup_jobs(tenant_slug)
    WHERE tenant_slug IS NOT NULL
      AND status IN ('PENDING', 'RUNNING', 'RESTORING');

CREATE UNIQUE INDEX IF NOT EXISTS uq_backup_jobs_active_full_database
    ON public.backup_jobs(scope)
    WHERE scope = 'FULL_DATABASE'
      AND status IN ('PENDING', 'RUNNING', 'RESTORING');

INSERT INTO public.system_permissions (code, module, description, active, created_at)
VALUES
    ('backups.create', 'BACKUPS', 'Create tenant backups', true, NOW()),
    ('backups.list', 'BACKUPS', 'List tenant backups', true, NOW()),
    ('backups.detail', 'BACKUPS', 'View tenant backup detail', true, NOW()),
    ('backups.download', 'BACKUPS', 'Download tenant backup artifact', true, NOW()),
    ('backups.restore', 'BACKUPS', 'Restore tenant backup', true, NOW())
ON CONFLICT (code) DO NOTHING;

```

### `V9__create_reporting_security.sql`

```sql
-- =====================================================================
-- Reporting security layer (Fase 1) — read-only roles, reporting schema
-- and the cross-tenant view regenerator.
--
-- Passwords are injected via Flyway placeholders
-- (spring.flyway.placeholders.tenant_readonly_password / platform_readonly_password),
-- which are wired from TENANT_READONLY_DB_PASSWORD / PLATFORM_READONLY_DB_PASSWORD.
-- =====================================================================

-- --------------------------------------------------------------------
-- 1. Read-only roles (defense in depth). No INSERT/UPDATE/DELETE/DDL.
--    NOINHERIT so they never pick up privileges from other roles.
-- --------------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'finance_tenant_readonly') THEN
        CREATE ROLE finance_tenant_readonly LOGIN PASSWORD '${tenant_readonly_password}'
            NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'finance_platform_readonly') THEN
        CREATE ROLE finance_platform_readonly LOGIN PASSWORD '${platform_readonly_password}'
            NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;
    END IF;
END $$;

-- Strip any inherited grants; they only get exactly what we hand out below.
REVOKE ALL ON SCHEMA public FROM finance_tenant_readonly;
REVOKE ALL ON SCHEMA public FROM finance_platform_readonly;
GRANT CONNECT ON DATABASE finance_db TO finance_tenant_readonly;
GRANT CONNECT ON DATABASE finance_db TO finance_platform_readonly;

-- --------------------------------------------------------------------
-- 2. Cross-tenant reporting schema (platform / SuperAdmin surface).
--    Only finance_platform_readonly may read here.
-- --------------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS reporting;
GRANT USAGE ON SCHEMA reporting TO finance_platform_readonly;

-- --------------------------------------------------------------------
-- 3. reporting.regenerate_views(): rebuilds the cross-tenant UNION ALL
--    views from every tenant_* schema that exposes the source view.
--    - quote_ident via format() %I/%L (no identifier injection in DDL)
--    - pg_advisory_xact_lock so concurrent provision/restore don't race
--    - skips tenant schemas missing a source view (incomplete migration)
--    - handles the zero-tenant case with typed empty views
-- --------------------------------------------------------------------
CREATE OR REPLACE FUNCTION reporting.regenerate_views()
RETURNS void
LANGUAGE plpgsql
AS $func$
DECLARE
    v_schema         text;
    v_slug           text;
    v_overview_parts text[] := ARRAY[]::text[];
    v_ranking_parts  text[] := ARRAY[]::text[];
    v_sql            text;
BEGIN
    -- Transaction-scoped advisory lock: auto-released on commit/rollback.
    PERFORM pg_advisory_xact_lock(918273);

    FOR v_schema IN
        SELECT schema_name
        FROM information_schema.schemata
        WHERE schema_name LIKE 'tenant\_%'
        ORDER BY schema_name
    LOOP
        v_slug := substring(v_schema FROM 8); -- strip leading 'tenant_'

        IF EXISTS (
            SELECT 1 FROM information_schema.views
            WHERE table_schema = v_schema
              AND table_name = 'reporting_tenant_executive_overview'
        ) THEN
            v_overview_parts := v_overview_parts || format(
                'SELECT %L::text AS tenant_slug, o.* FROM %I.reporting_tenant_executive_overview o',
                v_slug, v_schema
            );
        END IF;

        IF EXISTS (
            SELECT 1 FROM information_schema.views
            WHERE table_schema = v_schema
              AND table_name = 'reporting_tenant_movements'
        ) THEN
            v_ranking_parts := v_ranking_parts || format(
                'SELECT %L::text AS tenant_slug, '
                || 'COUNT(*)::bigint AS transaction_count, '
                || 'COALESCE(SUM(m.amount), 0)::numeric AS total_amount, '
                || 'MAX(m.created_at) AS last_transaction_at '
                || 'FROM %I.reporting_tenant_movements m',
                v_slug, v_schema
            );
        END IF;
    END LOOP;

    -- reporting.platform_overview
    IF array_length(v_overview_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::bigint AS active_users, '
              || 'NULL::bigint AS active_accounts, NULL::numeric AS total_available_balance, '
              || 'NULL::numeric AS total_held_balance, NULL::bigint AS completed_transactions, '
              || 'NULL::numeric AS completed_volume WHERE false';
    ELSE
        v_sql := array_to_string(v_overview_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.platform_overview';
    EXECUTE 'CREATE VIEW reporting.platform_overview AS ' || v_sql;

    -- reporting.tenant_movement_ranking
    IF array_length(v_ranking_parts, 1) IS NULL THEN
        v_sql := 'SELECT NULL::text AS tenant_slug, NULL::bigint AS transaction_count, '
              || 'NULL::numeric AS total_amount, NULL::timestamptz AS last_transaction_at WHERE false';
    ELSE
        v_sql := array_to_string(v_ranking_parts, ' UNION ALL ');
    END IF;
    EXECUTE 'DROP VIEW IF EXISTS reporting.tenant_movement_ranking';
    EXECUTE 'CREATE VIEW reporting.tenant_movement_ranking AS ' || v_sql;

    -- Platform read-only role only ever sees reporting.* views.
    GRANT SELECT ON reporting.platform_overview        TO finance_platform_readonly;
    GRANT SELECT ON reporting.tenant_movement_ranking  TO finance_platform_readonly;
END;
$func$;

-- --------------------------------------------------------------------
-- 4. Build the views once now (empty if there are no tenants yet).
--    The Java backfill re-runs this after ensuring every existing
--    tenant has the V16 reporting_* views + per-view grants.
-- --------------------------------------------------------------------
SELECT reporting.regenerate_views();

```

