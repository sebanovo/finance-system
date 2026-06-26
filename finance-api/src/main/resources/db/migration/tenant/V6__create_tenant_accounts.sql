CREATE TABLE IF NOT EXISTS tenant_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    account_number VARCHAR(60) NOT NULL UNIQUE,

    account_name VARCHAR(40) NOT NULL,
    custom_alias VARCHAR(100),

    account_type VARCHAR(40) NOT NULL,
    currency VARCHAR(10) NOT NULL,

    available_balance NUMERIC(18, 2) NOT NULL DEFAULT 0,
    held_balance NUMERIC(18, 2) NOT NULL DEFAULT 0,

    status VARCHAR(40) NOT NULL DEFAULT 'ACTIVE',
    status_reason TEXT,

    active BOOLEAN NOT NULL DEFAULT TRUE,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,

    opened_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    closed_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tenant_accounts_user
        FOREIGN KEY (user_id)
        REFERENCES tenant_users(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_tenant_accounts_account_name
        CHECK (account_name IN (
            'MAIN_WALLET',
            'SAVINGS_ACCOUNT',
            'CHECKING_ACCOUNT',
            'CREDIT_CARD_ACCOUNT',
            'PREPAID_CARD_ACCOUNT',
            'LOAN_ACCOUNT',
            'BUSINESS_ACCOUNT',
            'SECONDARY_ACCOUNT'
        )),

    CONSTRAINT chk_tenant_accounts_type
        CHECK (account_type IN (
            'WALLET',
            'SAVINGS',
            'CHECKING',
            'CREDIT_CARD',
            'PREPAID_CARD',
            'LOAN'
        )),

    CONSTRAINT chk_tenant_accounts_currency
        CHECK (currency IN (
            'BOB',
            'USD',
            'EUR',
            'USDT'
        )),

    CONSTRAINT chk_tenant_accounts_status
        CHECK (status IN (
            'ACTIVE',
            'BLOCKED',
            'SUSPENDED',
            'FROZEN',
            'CLOSED',
            'PENDING_VERIFICATION',
            'PENDING_APPROVAL'
        )),

    CONSTRAINT chk_tenant_accounts_available_balance
        CHECK (available_balance >= 0),

    CONSTRAINT chk_tenant_accounts_held_balance
        CHECK (held_balance >= 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_accounts_user_id
    ON tenant_accounts(user_id);

CREATE INDEX IF NOT EXISTS idx_tenant_accounts_account_number
    ON tenant_accounts(account_number);

CREATE INDEX IF NOT EXISTS idx_tenant_accounts_account_name
    ON tenant_accounts(account_name);

CREATE INDEX IF NOT EXISTS idx_tenant_accounts_account_type
    ON tenant_accounts(account_type);

CREATE INDEX IF NOT EXISTS idx_tenant_accounts_currency
    ON tenant_accounts(currency);

CREATE INDEX IF NOT EXISTS idx_tenant_accounts_status
    ON tenant_accounts(status);

CREATE INDEX IF NOT EXISTS idx_tenant_accounts_user_primary
    ON tenant_accounts(user_id, is_primary);
