CREATE TABLE IF NOT EXISTS tenant_audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_subject VARCHAR(150),
    event_type VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100),
    resource_id VARCHAR(100),
    event_details TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);