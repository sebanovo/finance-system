-- Tenant schema initial migration: producto and ventas
CREATE TABLE IF NOT EXISTS producto (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    precio DOUBLE PRECISION,
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS venta (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT REFERENCES producto(id),
    cantidad INTEGER,
    total DOUBLE PRECISION,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
