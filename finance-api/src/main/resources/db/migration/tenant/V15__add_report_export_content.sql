ALTER TABLE tenant_report_exports
    ADD COLUMN IF NOT EXISTS file_content BYTEA NULL;
