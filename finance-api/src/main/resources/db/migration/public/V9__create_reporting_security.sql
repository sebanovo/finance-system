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
