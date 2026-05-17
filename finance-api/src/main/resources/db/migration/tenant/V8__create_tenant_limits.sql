CREATE TABLE IF NOT EXISTS tenant_limit_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    code VARCHAR(120) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description TEXT,

    limit_type VARCHAR(40) NOT NULL,
    scope_type VARCHAR(40) NOT NULL,
    period VARCHAR(20) NOT NULL,

    transaction_type VARCHAR(40),
    account_type VARCHAR(40),
    currency VARCHAR(10),

    min_amount NUMERIC(19, 4),
    max_amount NUMERIC(19, 4),
    max_count BIGINT,

    active BOOLEAN NOT NULL DEFAULT TRUE,
    require_review_exceed BOOLEAN NOT NULL DEFAULT FALSE,

    created_by_user_id UUID,
    updated_by_user_id UUID,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_tenant_limit_rules_limit_type
        CHECK (limit_type IN (
            'PER_TRANSACTION_AMOUNT',
            'DAILY_AMOUNT',
            'MONTHLY_AMOUNT',
            'DAILY_COUNT',
            'MONTHLY_COUNT',
            'MIN_AMOUNT',
            'MAX_BALANCE'
        )),

    CONSTRAINT chk_tenant_limit_rules_scope_type
        CHECK (scope_type IN (
            'TENANT',
            'ACCOUNT_TYPE',
            'TRANSACTION_TYPE',
            'USER',
            'ACCOUNT'
        )),

    CONSTRAINT chk_tenant_limit_rules_period
        CHECK (period IN (
            'TRANSACTION',
            'DAILY',
            'MONTHLY'
        )),

    CONSTRAINT chk_tenant_limit_rules_transaction_type
        CHECK (
            transaction_type IS NULL OR transaction_type IN (
                'TRANSFER',
                'DEPOSIT',
                'WITHDRAWAL',
                'PAYMENT',
                'REVERSAL',
                'REFUND',
                'FEE',
                'ADJUSTMENT',
                'HOLD',
                'RELEASE',
                'SETTLEMENT'
            )
        ),

    CONSTRAINT chk_tenant_limit_rules_account_type
        CHECK (
            account_type IS NULL OR account_type IN (
                'WALLET',
                'SAVINGS',
                'CHECKING',
                'CREDIT_CARD',
                'PREPAID_CARD',
                'LOAN'
            )
        ),

    CONSTRAINT chk_tenant_limit_rules_currency
        CHECK (
            currency IS NULL OR currency IN (
                'BOB',
                'USD',
                'EUR',
                'USDT'
            )
        ),

    CONSTRAINT chk_tenant_limit_rules_amounts
        CHECK (
            (min_amount IS NULL OR min_amount >= 0)
            AND (max_amount IS NULL OR max_amount >= 0)
            AND (max_count IS NULL OR max_count >= 0)
        )
);

CREATE INDEX IF NOT EXISTS idx_tenant_limit_rules_active
    ON tenant_limit_rules(active);

CREATE INDEX IF NOT EXISTS idx_tenant_limit_rules_scope
    ON tenant_limit_rules(scope_type, limit_type, period);

CREATE INDEX IF NOT EXISTS idx_tenant_limit_rules_transaction_type
    ON tenant_limit_rules(transaction_type);

CREATE INDEX IF NOT EXISTS idx_tenant_limit_rules_account_type
    ON tenant_limit_rules(account_type);

CREATE INDEX IF NOT EXISTS idx_tenant_limit_rules_currency
    ON tenant_limit_rules(currency);


CREATE TABLE IF NOT EXISTS tenant_limit_usages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    limit_rule_id UUID NOT NULL,
    scope_key VARCHAR(180) NOT NULL,
    period_key VARCHAR(40) NOT NULL,

    transaction_count BIGINT NOT NULL DEFAULT 0,
    total_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,

    last_evaluated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tenant_limit_usages_rule
        FOREIGN KEY (limit_rule_id)
        REFERENCES tenant_limit_rules(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_tenant_limit_usages_rule_scope_period
        UNIQUE (limit_rule_id, scope_key, period_key),

    CONSTRAINT chk_tenant_limit_usages_counters
        CHECK (
            transaction_count >= 0
            AND total_amount >= 0
        )
);

CREATE INDEX IF NOT EXISTS idx_tenant_limit_usages_scope_period
    ON tenant_limit_usages(scope_key, period_key);

