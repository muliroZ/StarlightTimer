CREATE TABLE anotacao (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    sessao_id UUID NULL,
    conteudo TEXT NOT NULL,
    data_anotacao DATE NOT NULL,
    criado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_sessao_id FOREIGN KEY (sessao_id) REFERENCES sessao(id)
);