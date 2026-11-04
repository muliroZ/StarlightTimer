CREATE TABLE sessao (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    tipo VARCHAR(25) NOT NULL,
    status VARCHAR(20) NOT NULL,
    duracao_planejada INTEGER NOT NULL DEFAULT 3600,
    tempo_de_foco INTEGER NOT NULL,
    tempo_de_pausa INTEGER NOT NULL,
    dia_local DATE NOT NULL,
    iniciado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    terminado_em TIMESTAMPTZ NULL,

    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT tipo_check CHECK ( tipo IN ('FOCUS', 'SHORT_BREAK', 'LONG_BREAK') ),
    CONSTRAINT status_check CHECK ( status IN ('RUNNING', 'PAUSED', 'COMPLETED', 'ABANDONED') )
);

CREATE TABLE sessao_evento (
    id UUID PRIMARY KEY,
    sessao_id UUID NOT NULL,
    payload TEXT NOT NULL,
    tipo_evento VARCHAR(25) NOT NULL,
    ocorrido_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    reportado_em TIMESTAMPTZ,
    origem VARCHAR(25) NOT NULL,

    CONSTRAINT fk_sessao_id FOREIGN KEY (sessao_id) REFERENCES sessao(id),
    CONSTRAINT tipo_evento_check CHECK ( tipo_evento IN
        ('INICIADA', 'PAUSADA', 'RETOMADA', 'CONCLUIDA', 'CONCLUSAO_REJEITADA', 'ABANDONADA')
    ),
    CONSTRAINT origem_check CHECK ( origem IN ('CLIENTE', 'SERVIDOR', 'AGENDADOR') )
);

CREATE UNIQUE INDEX idx_sessao_ativa ON sessao (usuario_id) WHERE status IN ('RUNNING', 'PAUSED');
CREATE INDEX idx_session_desc ON sessao (usuario_id, iniciado_em DESC);
CREATE INDEX idx_reported_session ON sessao_evento (sessao_id, reportado_em);