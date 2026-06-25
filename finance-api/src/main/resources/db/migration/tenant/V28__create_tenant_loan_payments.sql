CREATE TABLE IF NOT EXISTS tenant_loan_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    loan_id UUID NOT NULL,

    amount NUMERIC(18, 2) NOT NULL,
    transaction_id UUID NULL,

    paid_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tenant_loan_payments_loan
        FOREIGN KEY (loan_id)
        REFERENCES tenant_loans(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_tenant_loan_payments_amount
        CHECK (amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_loan_payments_loan_id
    ON tenant_loan_payments(loan_id);
