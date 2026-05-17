CREATE TABLE IF NOT EXISTS tenant_qr_transaction_intents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    status VARCHAR(20) NOT NULL,
    channel VARCHAR(30) NOT NULL DEFAULT 'QR',

    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL,

    target_account_id UUID NOT NULL,
    external_reference VARCHAR(150),
    description VARCHAR(255),
    idempotency_key VARCHAR(150) NOT NULL,

    confirmed_transaction_id UUID NULL,
    requested_by_user_id UUID NOT NULL,
    confirmed_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_tenant_qr_transaction_intents_idempotency
        UNIQUE (requested_by_user_id, idempotency_key),

    CONSTRAINT fk_tenant_qr_transaction_intents_target_account
        FOREIGN KEY (target_account_id)
        REFERENCES tenant_accounts(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_tenant_qr_transaction_intents_confirmed_transaction
        FOREIGN KEY (confirmed_transaction_id)
        REFERENCES tenant_transactions(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_tenant_qr_transaction_intents_status
        CHECK (status IN (
            'PENDING',
            'CONFIRMED',
            'CANCELLED',
            'EXPIRED'
        )),

    CONSTRAINT chk_tenant_qr_transaction_intents_channel
        CHECK (channel IN (
            'QR'
        )),

    CONSTRAINT chk_tenant_qr_transaction_intents_amount
        CHECK (amount >= 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_qr_transaction_intents_status
    ON tenant_qr_transaction_intents(status);

CREATE INDEX IF NOT EXISTS idx_tenant_qr_transaction_intents_target_account
    ON tenant_qr_transaction_intents(target_account_id);

CREATE INDEX IF NOT EXISTS idx_tenant_qr_transaction_intents_requested_by_user
    ON tenant_qr_transaction_intents(requested_by_user_id);
