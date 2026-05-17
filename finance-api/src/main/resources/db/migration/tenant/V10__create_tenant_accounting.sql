CREATE TABLE IF NOT EXISTS tenant_accounting_periods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    period_code VARCHAR(80) NOT NULL UNIQUE,
    period_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',

    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    closed_at TIMESTAMPTZ NULL,

    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_tenant_accounting_periods_type
        CHECK (period_type IN (
            'DAILY',
            'MONTHLY',
            'ANNUAL',
            'CUSTOM'
        )),

    CONSTRAINT chk_tenant_accounting_periods_status
        CHECK (status IN (
            'OPEN',
            'CLOSED',
            'ARCHIVED'
        ))
);

CREATE INDEX IF NOT EXISTS idx_tenant_accounting_periods_status
    ON tenant_accounting_periods(status);


CREATE TABLE IF NOT EXISTS tenant_journal_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    entry_number VARCHAR(80) NOT NULL UNIQUE,
    source_transaction_id UUID NULL,
    period_id UUID NULL,

    entry_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'POSTED',

    description VARCHAR(255),
    reference VARCHAR(150),
    total_debits NUMERIC(19, 4) NOT NULL DEFAULT 0,
    total_credits NUMERIC(19, 4) NOT NULL DEFAULT 0,
    posted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tenant_journal_entries_period
        FOREIGN KEY (period_id)
        REFERENCES tenant_accounting_periods(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_tenant_journal_entries_transaction
        FOREIGN KEY (source_transaction_id)
        REFERENCES tenant_transactions(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_tenant_journal_entries_type
        CHECK (entry_type IN (
            'TRANSFER',
            'DEPOSIT',
            'WITHDRAWAL',
            'PAYMENT',
            'REVERSAL',
            'REFUND',
            'FEE',
            'ADJUSTMENT',
            'SETTLEMENT'
        )),

    CONSTRAINT chk_tenant_journal_entries_status
        CHECK (status IN (
            'DRAFT',
            'POSTED',
            'REVERSED',
            'VOID'
        )),

    CONSTRAINT chk_tenant_journal_entries_totals
        CHECK (total_debits >= 0 AND total_credits >= 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_journal_entries_entry_type
    ON tenant_journal_entries(entry_type);

CREATE INDEX IF NOT EXISTS idx_tenant_journal_entries_status
    ON tenant_journal_entries(status);

CREATE INDEX IF NOT EXISTS idx_tenant_journal_entries_posted_at
    ON tenant_journal_entries(posted_at DESC);


CREATE TABLE IF NOT EXISTS tenant_journal_lines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    journal_entry_id UUID NOT NULL,
    line_no INT NOT NULL,

    account_code VARCHAR(60) NOT NULL,
    account_name VARCHAR(120) NOT NULL,
    line_type VARCHAR(20) NOT NULL,

    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL,

    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tenant_journal_lines_entry
        FOREIGN KEY (journal_entry_id)
        REFERENCES tenant_journal_entries(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_tenant_journal_lines_entry_no
        UNIQUE (journal_entry_id, line_no),

    CONSTRAINT chk_tenant_journal_lines_type
        CHECK (line_type IN (
            'DEBIT',
            'CREDIT'
        )),

    CONSTRAINT chk_tenant_journal_lines_amount
        CHECK (amount >= 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_journal_lines_entry
    ON tenant_journal_lines(journal_entry_id);

