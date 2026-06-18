-- =====================================================================
-- Reporting catalog batch 4 (Fase 6) — remaining tenant views.
-- Columns verified against tenant_limit_rules, tenant_journal_*, tenant_fx_*,
-- tenant_users + tenant_transactions. Free-text descriptions are omitted.
-- =====================================================================

-- Limits, alerts and review.
CREATE OR REPLACE VIEW reporting_tenant_limits_alerts AS
SELECT
    r.id                    AS rule_id,
    r.code                  AS code,
    r.name                  AS name,
    r.limit_type            AS limit_type,
    r.scope_type            AS scope_type,
    r.period                AS period,
    r.transaction_type      AS transaction_type,
    r.account_type          AS account_type,
    r.currency              AS currency,
    r.min_amount            AS min_amount,
    r.max_amount            AS max_amount,
    r.max_count             AS max_count,
    r.active                AS active,
    r.require_review_exceed AS require_review_exceed,
    r.created_at            AS created_at
FROM tenant_limit_rules r;

-- Accounting ledger (journal lines + their entry).
CREATE OR REPLACE VIEW reporting_tenant_accounting_ledger AS
SELECT
    l.id            AS line_id,
    e.entry_number  AS entry_number,
    e.entry_type    AS entry_type,
    e.status        AS status,
    e.posted_at     AS posted_at,
    l.line_no       AS line_no,
    l.account_code  AS account_code,
    l.account_name  AS account_name,
    l.line_type     AS line_type,
    l.amount        AS amount,
    l.currency      AS currency
FROM tenant_journal_lines l
JOIN tenant_journal_entries e ON e.id = l.journal_entry_id;

-- FX rates and operation fees (unified).
CREATE OR REPLACE VIEW reporting_tenant_fx_fees AS
SELECT
    'RATE'::text             AS item_type,
    r.source_currency        AS source_currency,
    r.target_currency        AS target_currency,
    NULL::varchar(60)        AS operation_code,
    NULL::varchar(20)        AS fee_type,
    r.rate                   AS value,
    r.active                 AS active,
    r.created_at             AS created_at
FROM tenant_fx_exchange_rates r
UNION ALL
SELECT
    'FEE'::text              AS item_type,
    NULL::varchar(10),
    NULL::varchar(10),
    f.operation_code,
    f.fee_type,
    f.fee_value,
    f.active,
    f.created_at
FROM tenant_operation_fees f;

-- Customer / user activity (derived from transactions).
CREATE OR REPLACE VIEW reporting_tenant_customer_activity AS
SELECT
    u.id                          AS user_id,
    u.email                       AS email,
    u.first_name                  AS first_name,
    u.last_name                   AS last_name,
    COUNT(t.id)                   AS transaction_count,
    COALESCE(SUM(t.amount), 0)    AS total_amount,
    MAX(t.created_at)             AS last_activity
FROM tenant_users u
LEFT JOIN tenant_transactions t ON t.requested_by_user_id = u.id
GROUP BY u.id, u.email, u.first_name, u.last_name;
