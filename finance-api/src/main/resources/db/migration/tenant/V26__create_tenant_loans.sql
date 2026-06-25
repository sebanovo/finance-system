CREATE TABLE IF NOT EXISTS tenant_loans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,
    account_id UUID NOT NULL,

    principal NUMERIC(18, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,

    annual_interest_rate NUMERIC(6, 2) NOT NULL,
    term_months INTEGER NOT NULL,
    interest_method VARCHAR(20) NOT NULL DEFAULT 'FLAT',

    status VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    purpose VARCHAR(255),
    status_reason TEXT,

    disbursed_at TIMESTAMPTZ NULL,
    closed_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tenant_loans_user
        FOREIGN KEY (user_id)
        REFERENCES tenant_users(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_tenant_loans_account
        FOREIGN KEY (account_id)
        REFERENCES tenant_accounts(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_tenant_loans_currency
        CHECK (currency IN ('BOB', 'USD', 'EUR', 'USDT')),

    CONSTRAINT chk_tenant_loans_interest_method
        CHECK (interest_method IN ('FLAT', 'FRENCH')),

    CONSTRAINT chk_tenant_loans_status
        CHECK (status IN (
            'REQUESTED',
            'APPROVED',
            'REJECTED',
            'DISBURSED',
            'PAID_OFF',
            'CANCELLED'
        )),

    CONSTRAINT chk_tenant_loans_principal
        CHECK (principal > 0),

    CONSTRAINT chk_tenant_loans_term_months
        CHECK (term_months > 0),

    CONSTRAINT chk_tenant_loans_annual_interest_rate
        CHECK (annual_interest_rate >= 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_loans_user_id
    ON tenant_loans(user_id);

CREATE INDEX IF NOT EXISTS idx_tenant_loans_account_id
    ON tenant_loans(account_id);

CREATE INDEX IF NOT EXISTS idx_tenant_loans_status
    ON tenant_loans(status);
