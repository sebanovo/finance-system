-- =====================================================================
-- Reporting catalog batch 2 (Fase 6) — additional tenant reporting_* views.
-- Derived from tenant_transactions (columns already verified). Masked: no raw
-- references / idempotency keys are exposed (aggregates only).
--
-- New views are granted automatically by ReportingSecurityService (it grants
-- every reporting_* view it finds), and cross-tenant platform views are rebuilt
-- by reporting.regenerate_views().
-- =====================================================================

-- Income vs expenses: completed transactions grouped by type + currency.
CREATE OR REPLACE VIEW reporting_tenant_income_expenses AS
SELECT
    t.type               AS transaction_type,
    t.currency           AS currency,
    COUNT(*)             AS transaction_count,
    COALESCE(SUM(t.amount), 0) AS total_amount
FROM tenant_transactions t
WHERE t.status = 'COMPLETED'
GROUP BY t.type, t.currency;

-- Transactions analysis: all transactions grouped by status + type + channel.
CREATE OR REPLACE VIEW reporting_tenant_transactions_analysis AS
SELECT
    t.status             AS status,
    t.type               AS transaction_type,
    t.channel            AS channel,
    t.currency           AS currency,
    COUNT(*)             AS transaction_count,
    COALESCE(SUM(t.amount), 0) AS total_amount
FROM tenant_transactions t
GROUP BY t.status, t.type, t.channel, t.currency;
