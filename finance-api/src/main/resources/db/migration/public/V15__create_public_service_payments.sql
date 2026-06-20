CREATE TABLE IF NOT EXISTS public.service_providers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(80) NOT NULL,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(40) NOT NULL,
    service_customer_code_label VARCHAR(100) NOT NULL DEFAULT 'Código de cliente',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_service_providers_code UNIQUE (code),
    CONSTRAINT chk_service_provider_code_uppercase CHECK (code = upper(btrim(code))),
    CONSTRAINT chk_service_provider_category CHECK (category IN (
        'ELECTRICITY',
        'WATER',
        'INTERNET',
        'TV_CABLE'
    )),
    CONSTRAINT chk_service_provider_status CHECK (status IN (
        'ACTIVE',
        'INACTIVE'
    ))
);

CREATE INDEX IF NOT EXISTS idx_service_providers_category
    ON public.service_providers(category);

CREATE INDEX IF NOT EXISTS idx_service_providers_status
    ON public.service_providers(status);

CREATE TABLE IF NOT EXISTS public.service_customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id UUID NOT NULL REFERENCES public.service_providers(id),
    service_customer_code VARCHAR(100) NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_service_customer_provider_code UNIQUE (provider_id, service_customer_code),
    CONSTRAINT chk_service_customer_status CHECK (status IN (
        'ACTIVE',
        'INACTIVE'
    ))
);

CREATE INDEX IF NOT EXISTS idx_service_customers_provider_id
    ON public.service_customers(provider_id);

CREATE INDEX IF NOT EXISTS idx_service_customers_service_customer_code
    ON public.service_customers(service_customer_code);

CREATE INDEX IF NOT EXISTS idx_service_customers_status
    ON public.service_customers(status);

CREATE TABLE IF NOT EXISTS public.service_bills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id UUID NOT NULL REFERENCES public.service_providers(id),
    service_customer_id UUID NOT NULL REFERENCES public.service_customers(id),
    service_customer_code VARCHAR(100) NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    billing_period VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'BOB',
    due_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    paid_by_tenant_id UUID NULL REFERENCES public.platform_tenants(id),
    paid_by_tenant_slug VARCHAR(100) NULL,
    paid_by_user_id UUID NULL,
    paid_by_account_id UUID NULL,
    paid_by_account_number VARCHAR(50) NULL,
    paid_transaction_id UUID NULL,
    paid_at TIMESTAMPTZ NULL,
    created_by_superadmin_id UUID NULL REFERENCES public.platform_superadmins(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_service_bill_provider_customer_period UNIQUE (provider_id, service_customer_code, billing_period),
    CONSTRAINT chk_service_bill_amount CHECK (amount > 0),
    CONSTRAINT chk_service_bill_status CHECK (status IN (
        'PENDING',
        'PAID',
        'EXPIRED',
        'CANCELLED',
        'REVERSED'
    ))
);

CREATE INDEX IF NOT EXISTS idx_service_bills_provider_id
    ON public.service_bills(provider_id);

CREATE INDEX IF NOT EXISTS idx_service_bills_service_customer_code
    ON public.service_bills(service_customer_code);

CREATE INDEX IF NOT EXISTS idx_service_bills_status
    ON public.service_bills(status);

CREATE INDEX IF NOT EXISTS idx_service_bills_billing_period
    ON public.service_bills(billing_period);

CREATE INDEX IF NOT EXISTS idx_service_bills_due_date
    ON public.service_bills(due_date);

CREATE INDEX IF NOT EXISTS idx_service_bills_paid_by_tenant_slug
    ON public.service_bills(paid_by_tenant_slug);

CREATE INDEX IF NOT EXISTS idx_service_bills_paid_transaction_id
    ON public.service_bills(paid_transaction_id);

CREATE TABLE IF NOT EXISTS public.service_bill_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID NOT NULL REFERENCES public.service_bills(id),
    provider_id UUID NOT NULL REFERENCES public.service_providers(id),
    paid_by_tenant_id UUID NOT NULL REFERENCES public.platform_tenants(id),
    paid_by_tenant_slug VARCHAR(100) NOT NULL,
    paid_by_user_id UUID NOT NULL,
    paid_by_account_id UUID NOT NULL,
    paid_by_account_number VARCHAR(50) NOT NULL,
    paid_transaction_id UUID NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    receipt_number VARCHAR(100) NOT NULL,
    idempotency_key VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PAID',
    paid_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_service_bill_payment_bill UNIQUE (bill_id),
    CONSTRAINT uq_service_bill_payment_receipt_number UNIQUE (receipt_number),
    CONSTRAINT uq_service_bill_payment_idempotency UNIQUE (paid_by_tenant_id, paid_by_user_id, idempotency_key),
    CONSTRAINT chk_service_bill_payment_status CHECK (status IN (
        'PAID',
        'REVERSED'
    )),
    CONSTRAINT chk_service_bill_payment_amount CHECK (amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_provider_id
    ON public.service_bill_payments(provider_id);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_paid_by_tenant_slug
    ON public.service_bill_payments(paid_by_tenant_slug);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_paid_by_user_id
    ON public.service_bill_payments(paid_by_user_id);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_paid_by_account_number
    ON public.service_bill_payments(paid_by_account_number);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_paid_transaction_id
    ON public.service_bill_payments(paid_transaction_id);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_receipt_number
    ON public.service_bill_payments(receipt_number);

CREATE INDEX IF NOT EXISTS idx_service_bill_payments_paid_at
    ON public.service_bill_payments(paid_at);

INSERT INTO public.service_providers (
    code,
    name,
    category,
    service_customer_code_label,
    status,
    created_at,
    updated_at
)
VALUES
    ('ELECTRICITY_CRE', 'CRE', 'ELECTRICITY', 'Código de suministro', 'ACTIVE', NOW(), NOW()),
    ('WATER_SAGUAPAC', 'SAGUAPAC', 'WATER', 'Código de suministro', 'ACTIVE', NOW(), NOW()),
    ('INTERNET_ENTEL', 'Entel Internet', 'INTERNET', 'Código de cliente', 'ACTIVE', NOW(), NOW()),
    ('TV_TIGO', 'Tigo TV', 'TV_CABLE', 'Número de abonado', 'ACTIVE', NOW(), NOW())
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    category = EXCLUDED.category,
    service_customer_code_label = EXCLUDED.service_customer_code_label,
    status = 'ACTIVE',
    updated_at = NOW();
