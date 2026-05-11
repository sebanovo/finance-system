CREATE TABLE IF NOT EXISTS tenant_account_sequences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    account_type VARCHAR(40) NOT NULL,
    currency VARCHAR(10) NOT NULL,

    current_value BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_tenant_account_sequences_type_currency
        UNIQUE (account_type, currency),

    CONSTRAINT chk_tenant_account_sequences_type
        CHECK (account_type IN (
            'WALLET',
            'SAVINGS',
            'CHECKING',
            'CREDIT_CARD',
            'PREPAID_CARD',
            'LOAN'
        )),

    CONSTRAINT chk_tenant_account_sequences_currency
        CHECK (currency IN (
            'BOB',
            'USD',
            'EUR',
            'USDT'
        )),

    CONSTRAINT chk_tenant_account_sequences_current_value
        CHECK (current_value >= 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_account_sequences_type_currency
    ON tenant_account_sequences(account_type, currency);