-- =====================================================================
-- Reporting security layer (Fase 1) — per-tenant masked reporting views.
--
-- Rule: every report (controlled or AI) reads ONLY these reporting_* views,
-- never raw tables. Sensitive columns (password_hash, idempotency_key,
-- external_reference, failure_reason, free-text reasons) are omitted here.
--
-- These views run with the owner's (finance_user) privileges, so the
-- read-only role finance_tenant_readonly only needs SELECT on the views
-- (granted at provisioning/backfill time), never on the underlying tables.
-- =====================================================================

-- Users, roles and activity (NO password_hash).
CREATE OR REPLACE VIEW reporting_tenant_users_activity AS
SELECT
    u.id            AS user_id,
    u.email         AS email,
    u.first_name    AS first_name,
    u.last_name     AS last_name,
    u.active        AS active,
    u.status        AS status,
    u.created_at    AS created_at,
    u.updated_at    AS updated_at
FROM tenant_users u;

-- Accounts and balances (omit custom_alias / status_reason free text).
CREATE OR REPLACE VIEW reporting_tenant_accounts_balances AS
SELECT
    a.id                                       AS account_id,
    a.user_id                                  AS user_id,
    a.account_number                           AS account_number,
    a.account_name                             AS account_name,
    a.account_type                             AS account_type,
    a.currency                                 AS currency,
    a.available_balance                        AS available_balance,
    a.held_balance                             AS held_balance,
    (a.available_balance + a.held_balance)     AS total_balance,
    a.status                                   AS status,
    a.active                                   AS active,
    a.is_primary                               AS is_primary,
    a.opened_at                                AS opened_at,
    a.closed_at                                AS closed_at,
    a.created_at                               AS created_at
FROM tenant_accounts a;

-- Financial movements (omit idempotency_key, external_reference,
-- failure_reason and free-text description).
CREATE OR REPLACE VIEW reporting_tenant_movements AS
SELECT
    t.id                                       AS transaction_id,
    t.created_at                               AS created_at,
    t.processed_at                             AS processed_at,
    t.type                                     AS transaction_type,
    t.status                                   AS status,
    t.channel                                  AS channel,
    t.amount                                   AS amount,
    t.currency                                 AS currency,
    t.source_account_id                        AS source_account_id,
    t.target_account_id                        AS target_account_id,
    t.requested_by_user_id                     AS requested_by_user_id,
    t.approved_by_user_id                      AS approved_by_user_id,
    (t.reversed_transaction_id IS NOT NULL)    AS is_reversal
FROM tenant_transactions t;

-- Single-row executive overview (aggregates only). Column order/names MUST
-- stay stable across tenants: reporting.regenerate_views() unions o.* over
-- this view to build reporting.platform_overview.
CREATE OR REPLACE VIEW reporting_tenant_executive_overview AS
SELECT
    (SELECT COUNT(*) FROM tenant_users    WHERE active)                        AS active_users,
    (SELECT COUNT(*) FROM tenant_accounts WHERE active)                        AS active_accounts,
    (SELECT COALESCE(SUM(available_balance), 0) FROM tenant_accounts)          AS total_available_balance,
    (SELECT COALESCE(SUM(held_balance), 0)      FROM tenant_accounts)          AS total_held_balance,
    (SELECT COUNT(*) FROM tenant_transactions   WHERE status = 'COMPLETED')    AS completed_transactions,
    (SELECT COALESCE(SUM(amount), 0) FROM tenant_transactions
        WHERE status = 'COMPLETED')                                           AS completed_volume;
