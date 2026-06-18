-- =====================================================================
-- Reporting catalog batch 3 (Fase 6) — tenant audit view.
-- Masks PII-heavy / free-text columns: omits ip_address, user_agent,
-- event_details, before_state, after_state, actor_subject.
-- =====================================================================

CREATE OR REPLACE VIEW reporting_tenant_audit AS
SELECT
    a.id            AS audit_id,
    a.created_at    AS created_at,
    a.actor_email   AS actor_email,
    a.event_type    AS event_type,
    a.resource_type AS resource_type,
    a.resource_id   AS resource_id,
    a.source        AS source,
    a.outcome       AS outcome
FROM tenant_audit_events a;
