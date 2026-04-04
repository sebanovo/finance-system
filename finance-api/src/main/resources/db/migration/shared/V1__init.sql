-- Public schema initial migration: tenants and users
-- CREATE TABLE IF NOT EXISTS tenant (
--     id VARCHAR(100) PRIMARY KEY,
--     display_name VARCHAR(255),
--     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
-- );
CREATE TABLE IF NOT EXISTS tenant (
    -- Id unico: empresa1, empresa2, etc.
    id VARCHAR(100) PRIMARY KEY,
    
    -- Información comercial/display
    name VARCHAR(255) NOT NULL,
    
    -- Fecha de vencimiento
    paid_until DATE,
    -- Esta en su periodo de prueba (true or false)
    on_trial BOOLEAN DEFAULT TRUE,
    
    -- Tenant Activo (true or false)
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    --- Fecha de creacion
    -- created_on DATE DEFAULT CURRENT_DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP

    
    -- -- Campos de configuración (para que tu Java sepa si debe crear/borrar)
    -- auto_create_schema BOOLEAN DEFAULT TRUE NOT NULL,
    -- auto_drop_schema BOOLEAN DEFAULT FALSE NOT NULL
);