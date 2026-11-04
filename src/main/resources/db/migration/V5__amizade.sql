CREATE TABLE pedido_amizade (
    id UUID PRIMARY KEY,
    remetente_id UUID NOT NULL,
    destinatario_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    enviado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    respondido_em TIMESTAMPTZ NULL,

    CONSTRAINT fk_remetente_id FOREIGN KEY (remetente_id) REFERENCES usuario(id),
    CONSTRAINT fk_destinatario_id FOREIGN KEY (destinatario_id) REFERENCES usuario(id),
    CONSTRAINT status_check CHECK ( status IN ('ACEITO', 'RECUSADO', 'PENDENTE', 'CANCELADO') ),
    CONSTRAINT pessoas_distintas CHECK ( remetente_id <> destinatario_id )
);

CREATE TABLE amizade (
    id UUID PRIMARY KEY,
    usuario1_id UUID NOT NULL,
    usuario2_id UUID NOT NULL,
    estabelecida_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario1_id FOREIGN KEY (usuario1_id) REFERENCES usuario(id),
    CONSTRAINT fk_usuario2_id FOREIGN KEY (usuario2_id) REFERENCES usuario(id),
    CONSTRAINT ordem_unica CHECK ( usuario1_id < usuario2_id ),
    CONSTRAINT par_unico UNIQUE (usuario1_id, usuario2_id)
);

CREATE TABLE lista_bloqueio (
    usuario_id UUID NOT NULL,
    bloqueado_id UUID NOT NULL,

    CONSTRAINT pk_usuario_bloqueado_id PRIMARY KEY (usuario_id, bloqueado_id),
    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_bloqueado_id FOREIGN KEY (bloqueado_id) REFERENCES usuario(id),
    CONSTRAINT pessoas_distintas CHECK ( usuario_id <> bloqueado_id )
);

CREATE UNIQUE INDEX uq_remetente_destinatario
    ON pedido_amizade (
        LEAST(remetente_id, destinatario_id),
        GREATEST(remetente_id, destinatario_id)
    ) WHERE status = 'PENDENTE';

CREATE OR REPLACE FUNCTION fn_aceitar_pedido_amizade()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO amizade (id, usuario1_id, usuario2_id, estabelecida_em)
    VALUES (gen_random_uuid(),
            LEAST(NEW.remetente_id, NEW.destinatario_id),
            GREATEST(NEW.remetente_id, NEW.destinatario_id),
            now());

    DELETE FROM pedido_amizade WHERE id = NEW.id;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_aceitar_pedido_amizade
AFTER UPDATE OF status ON pedido_amizade
FOR EACH ROW WHEN ( new.status = 'ACEITO' AND old.status IS DISTINCT FROM 'ACEITO' )
EXECUTE FUNCTION fn_aceitar_pedido_amizade();