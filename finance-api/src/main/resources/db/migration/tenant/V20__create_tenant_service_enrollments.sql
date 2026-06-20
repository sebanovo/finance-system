CREATE TABLE IF NOT EXISTS tenant_service_enrollments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES tenant_users(id),
    provider_id UUID NOT NULL REFERENCES public.service_providers(id),
    provider_code VARCHAR(80) NOT NULL,
    provider_name VARCHAR(150) NOT NULL,
    provider_category VARCHAR(40) NOT NULL,
    service_customer_code VARCHAR(100) NOT NULL,
    service_customer_name VARCHAR(150) NOT NULL,
    alias VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_tenant_service_enrollment_user_provider_code UNIQUE (user_id, provider_id, service_customer_code),
    CONSTRAINT chk_tenant_service_enrollment_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX IF NOT EXISTS idx_tenant_service_enrollments_user_id
    ON tenant_service_enrollments(user_id);

CREATE INDEX IF NOT EXISTS idx_tenant_service_enrollments_provider_id
    ON tenant_service_enrollments(provider_id);

CREATE INDEX IF NOT EXISTS idx_tenant_service_enrollments_status
    ON tenant_service_enrollments(status);

CREATE INDEX IF NOT EXISTS idx_tenant_service_enrollments_service_customer_code
    ON tenant_service_enrollments(service_customer_code);
