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
