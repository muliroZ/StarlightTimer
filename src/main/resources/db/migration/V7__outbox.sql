CREATE TABLE outbox (
    id UUID PRIMARY KEY,
    tipo_agregado VARCHAR(25),
    id_agregado UUID NOT NULL,
    tipo_evento VARCHAR(40),
    payload JSONB,
    criado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    publicado_em TIMESTAMPTZ DEFAULT NULL
);