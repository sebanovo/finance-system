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
