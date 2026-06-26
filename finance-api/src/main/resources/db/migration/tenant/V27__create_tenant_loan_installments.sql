CREATE TABLE IF NOT EXISTS tenant_loan_installments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    loan_id UUID NOT NULL,
    number INTEGER NOT NULL,

    due_date DATE NOT NULL,

    principal_due NUMERIC(18, 2) NOT NULL,
    interest_due NUMERIC(18, 2) NOT NULL,
    total_due NUMERIC(18, 2) NOT NULL,
    paid_amount NUMERIC(18, 2) NOT NULL DEFAULT 0,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tenant_loan_installments_loan
        FOREIGN KEY (loan_id)
        REFERENCES tenant_loans(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_tenant_loan_installments_loan_number
        UNIQUE (loan_id, number),

    CONSTRAINT chk_tenant_loan_installments_status
        CHECK (status IN ('PENDING', 'PARTIAL', 'PAID', 'OVERDUE')),

    CONSTRAINT chk_tenant_loan_installments_amounts
        CHECK (
            principal_due >= 0
            AND interest_due >= 0
            AND total_due >= 0
            AND paid_amount >= 0
        )
);

CREATE INDEX IF NOT EXISTS idx_tenant_loan_installments_loan_id
    ON tenant_loan_installments(loan_id);

CREATE INDEX IF NOT EXISTS idx_tenant_loan_installments_due_date
    ON tenant_loan_installments(due_date);

CREATE INDEX IF NOT EXISTS idx_tenant_loan_installments_status
    ON tenant_loan_installments(status);
