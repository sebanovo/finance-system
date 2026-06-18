-- =====================================================================
-- Add request_params to report_executions (controlled-report filters, used to
-- rerun controlled reports). Added as a separate migration because V10 was
-- already applied; never edit an applied migration.
-- =====================================================================

ALTER TABLE public.report_executions
    ADD COLUMN IF NOT EXISTS request_params JSONB;
