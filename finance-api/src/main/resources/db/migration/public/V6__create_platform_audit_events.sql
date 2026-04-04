CREATE TABLE IF NOT EXISTS public.platform_audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_subject VARCHAR(150),
    event_type VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100),
    resource_id VARCHAR(100),
    event_details TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_platform_audit_events_created_at
    ON public.platform_audit_events(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_platform_audit_events_event_type
    ON public.platform_audit_events(event_type);