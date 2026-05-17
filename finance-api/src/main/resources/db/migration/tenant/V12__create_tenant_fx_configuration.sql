CREATE TABLE IF NOT EXISTS tenant_fx_exchange_rates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    source_currency VARCHAR(10) NOT NULL,
    target_currency VARCHAR(10) NOT NULL,

    rate NUMERIC(19, 8) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_tenant_fx_exchange_rates_pair
        UNIQUE (source_currency, target_currency),

    CONSTRAINT chk_tenant_fx_exchange_rates_source_currency
        CHECK (source_currency IN (
            'BOB',
            'USD',
            'EUR',
            'USDT'
        )),

    CONSTRAINT chk_tenant_fx_exchange_rates_target_currency
        CHECK (target_currency IN (
            'BOB',
            'USD',
            'EUR',
            'USDT'
        )),

    CONSTRAINT chk_tenant_fx_exchange_rates_rate
        CHECK (rate > 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_fx_exchange_rates_active
    ON tenant_fx_exchange_rates(active);

CREATE INDEX IF NOT EXISTS idx_tenant_fx_exchange_rates_pair
    ON tenant_fx_exchange_rates(source_currency, target_currency);


CREATE TABLE IF NOT EXISTS tenant_operation_fees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    operation_code VARCHAR(60) NOT NULL,
    fee_type VARCHAR(20) NOT NULL DEFAULT 'NONE',
    fee_value NUMERIC(19, 8) NOT NULL DEFAULT 0,
    calculation_mode VARCHAR(20) NOT NULL DEFAULT 'SEPARATE',

    active BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_tenant_operation_fees_operation_code
        UNIQUE (operation_code),

    CONSTRAINT chk_tenant_operation_fees_fee_type
        CHECK (fee_type IN (
            'NONE',
            'FIXED',
            'PERCENTAGE'
        )),

    CONSTRAINT chk_tenant_operation_fees_fee_value
        CHECK (fee_value >= 0),

    CONSTRAINT chk_tenant_operation_fees_calculation_mode
        CHECK (calculation_mode IN (
            'SEPARATE',
            'INCLUDED'
        ))
);

CREATE INDEX IF NOT EXISTS idx_tenant_operation_fees_active
    ON tenant_operation_fees(active);

CREATE INDEX IF NOT EXISTS idx_tenant_operation_fees_operation_code
    ON tenant_operation_fees(operation_code);
