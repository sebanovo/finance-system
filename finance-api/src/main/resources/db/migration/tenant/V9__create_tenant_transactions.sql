CREATE TABLE IF NOT EXISTS tenant_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    channel VARCHAR(30) NOT NULL,

    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL,

    source_account_id UUID NULL,
    target_account_id UUID NULL,

    external_reference VARCHAR(150),
    idempotency_key VARCHAR(150) NOT NULL,

    description VARCHAR(255),
    failure_reason TEXT,
    metadata JSONB,

    parent_transaction_id UUID NULL,
    reversed_transaction_id UUID NULL,

    requested_by_user_id UUID NOT NULL,
    approved_by_user_id UUID NULL,

    processed_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_tenant_transactions_idempotency
        UNIQUE (requested_by_user_id, idempotency_key),

    CONSTRAINT fk_tenant_transactions_source_account
        FOREIGN KEY (source_account_id)
        REFERENCES tenant_accounts(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_tenant_transactions_target_account
        FOREIGN KEY (target_account_id)
        REFERENCES tenant_accounts(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_tenant_transactions_parent
        FOREIGN KEY (parent_transaction_id)
        REFERENCES tenant_transactions(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_tenant_transactions_reversed
        FOREIGN KEY (reversed_transaction_id)
        REFERENCES tenant_transactions(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_tenant_transactions_type
        CHECK (type IN (
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
        )),

    CONSTRAINT chk_tenant_transactions_status
        CHECK (status IN (
            'PENDING',
            'PENDING_REVIEW',
            'PROCESSING',
            'AUTHORIZED',
            'COMPLETED',
            'FAILED',
            'REVERSED',
            'PARTIALLY_REFUNDED',
            'CANCELLED',
            'EXPIRED'
        )),

    CONSTRAINT chk_tenant_transactions_channel
        CHECK (channel IN (
            'MANUAL',
            'QR',
            'API',
            'ADMIN',
            'CASHBOX',
            'SCHEDULED',
            'EXTERNAL_BANK',
            'SYSTEM'
        ))
);

CREATE INDEX IF NOT EXISTS idx_tenant_transactions_type
    ON tenant_transactions(type);

CREATE INDEX IF NOT EXISTS idx_tenant_transactions_status
    ON tenant_transactions(status);

CREATE INDEX IF NOT EXISTS idx_tenant_transactions_channel
    ON tenant_transactions(channel);

CREATE INDEX IF NOT EXISTS idx_tenant_transactions_source_account
    ON tenant_transactions(source_account_id);

CREATE INDEX IF NOT EXISTS idx_tenant_transactions_target_account
    ON tenant_transactions(target_account_id);

CREATE INDEX IF NOT EXISTS idx_tenant_transactions_requested_by_user
    ON tenant_transactions(requested_by_user_id);

CREATE INDEX IF NOT EXISTS idx_tenant_transactions_created_at
    ON tenant_transactions(created_at DESC);


CREATE TABLE IF NOT EXISTS tenant_transaction_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    transaction_id UUID NOT NULL,
    account_id UUID NOT NULL,

    movement_type VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL,

    balance_before NUMERIC(19, 4) NOT NULL,
    balance_after NUMERIC(19, 4) NOT NULL,

    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_transaction_movements_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES tenant_transactions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transaction_movements_account
        FOREIGN KEY (account_id)
        REFERENCES tenant_accounts(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_tenant_transaction_movements_type
        CHECK (movement_type IN (
            'DEBIT',
            'CREDIT',
            'HOLD',
            'RELEASE'
        )),

    CONSTRAINT chk_tenant_transaction_movements_amount
        CHECK (amount >= 0),

    CONSTRAINT chk_tenant_transaction_movements_balances
        CHECK (
            balance_before >= 0
            AND balance_after >= 0
        )
);

CREATE INDEX IF NOT EXISTS idx_tenant_transaction_movements_transaction
    ON tenant_transaction_movements(transaction_id);

CREATE INDEX IF NOT EXISTS idx_tenant_transaction_movements_account
    ON tenant_transaction_movements(account_id);

