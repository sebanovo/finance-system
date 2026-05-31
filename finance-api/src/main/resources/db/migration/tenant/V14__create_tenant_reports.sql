CREATE TABLE IF NOT EXISTS tenant_report_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    report_type VARCHAR(80) NOT NULL,
    report_title VARCHAR(150) NOT NULL,
    execution_name VARCHAR(180) NOT NULL,

    mode VARCHAR(30) NOT NULL,
    requested_by_subject VARCHAR(150),

    request_payload JSONB NOT NULL,
    outputs JSONB NOT NULL,
    row_count INT NOT NULL DEFAULT 0,

    source_execution_id UUID NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    error_message TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tenant_report_executions_source
        FOREIGN KEY (source_execution_id)
        REFERENCES tenant_report_executions(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_tenant_report_executions_mode
        CHECK (mode IN ('ANALYTIC', 'MANAGERIAL')),

    CONSTRAINT chk_tenant_report_executions_status
        CHECK (status IN ('COMPLETED', 'FAILED')),

    CONSTRAINT chk_tenant_report_executions_row_count
        CHECK (row_count >= 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_report_executions_type
    ON tenant_report_executions(report_type);

CREATE INDEX IF NOT EXISTS idx_tenant_report_executions_mode
    ON tenant_report_executions(mode);

CREATE INDEX IF NOT EXISTS idx_tenant_report_executions_status
    ON tenant_report_executions(status);

CREATE INDEX IF NOT EXISTS idx_tenant_report_executions_created_at
    ON tenant_report_executions(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_tenant_report_executions_outputs
    ON tenant_report_executions USING GIN (outputs);

CREATE INDEX IF NOT EXISTS idx_tenant_report_executions_request_payload
    ON tenant_report_executions USING GIN (request_payload);

CREATE INDEX IF NOT EXISTS idx_tenant_report_executions_source
    ON tenant_report_executions(source_execution_id);


CREATE TABLE IF NOT EXISTS tenant_report_exports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    execution_id UUID NOT NULL,

    output VARCHAR(20) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(120) NOT NULL,
    file_size_bytes BIGINT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tenant_report_exports_execution
        FOREIGN KEY (execution_id)
        REFERENCES tenant_report_executions(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_tenant_report_exports_output
        CHECK (output IN ('PDF', 'XLSX')),

    CONSTRAINT chk_tenant_report_exports_file_size
        CHECK (file_size_bytes >= 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_report_exports_execution
    ON tenant_report_exports(execution_id);

CREATE INDEX IF NOT EXISTS idx_tenant_report_exports_output
    ON tenant_report_exports(output);

CREATE INDEX IF NOT EXISTS idx_tenant_report_exports_created_at
    ON tenant_report_exports(created_at DESC);
