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
