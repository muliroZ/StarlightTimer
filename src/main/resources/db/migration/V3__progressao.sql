CREATE TABLE limite_nivel (
    nivel INTEGER PRIMARY KEY,
    xp_necessario INTEGER NOT NULL
);

CREATE TABLE patente (
    id UUID PRIMARY KEY,
    codigo VARCHAR(35) UNIQUE NOT NULL,
    nome VARCHAR(35) UNIQUE NOT NULL,
    nivel_minimo INTEGER UNIQUE NOT NULL,
    imagem_titulo TEXT NULL,

    CONSTRAINT fk_nivel_minimo FOREIGN KEY (nivel_minimo) REFERENCES limite_nivel(nivel)
);

CREATE TABLE insignia (
    id UUID PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nome VARCHAR(50) UNIQUE NOT NULL,
    descricao TEXT,
    icone TEXT,
    xp_recompensa INTEGER NOT NULL,
    criterio_json JSONB NOT NULL,
    tipo_criterio VARCHAR(25),
    contagem_alvo INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE insignia_usuario (
    usuario_id UUID NOT NULL,
    insignia_id UUID NOT NULL,
    desbloqueado_em TIMESTAMPTZ,
    contagem_progresso INTEGER NOT NULL,

    PRIMARY KEY (usuario_id, insignia_id),
    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_insignia_id FOREIGN KEY (insignia_id) REFERENCES insignia(id)
);

CREATE TABLE constelacao (
    id UUID PRIMARY KEY,
    codigo VARCHAR(30) UNIQUE NOT NULL,
    nome VARCHAR(30) UNIQUE NOT NULL,
    xp_recompensa INTEGER NOT NULL,
    qtd_estrelas INTEGER NOT NULL,
    qtd_insignias_requeridas INTEGER NOT NULL,
    descricao TEXT,
    asset_svg TEXT
);

CREATE TABLE insignia_constelacao (
    constelacao_id UUID NOT NULL,
    insignia_id UUID NOT NULL,

    PRIMARY KEY (constelacao_id, insignia_id),
    CONSTRAINT fk_constelacao_id FOREIGN KEY (constelacao_id) REFERENCES constelacao(id),
    CONSTRAINT fk_insignia_id FOREIGN KEY (insignia_id) REFERENCES insignia(id)
);

CREATE TABLE constelacao_usuario (
    constelacao_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    desbloqueado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (constelacao_id, usuario_id),
    CONSTRAINT fk_constelacao_id FOREIGN KEY (constelacao_id) REFERENCES constelacao(id),
    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE ganho_xp (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    quantidade INTEGER NOT NULL,
    origem_sessao UUID NULL,
    origem_constelacao UUID NULL,
    origem_insignia UUID NULL,
    atribuido_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_sessao_id FOREIGN KEY (origem_sessao) REFERENCES sessao(id),
    CONSTRAINT fk_constelacao_id FOREIGN KEY (origem_constelacao) REFERENCES constelacao(id),
    CONSTRAINT fk_insignia_id FOREIGN KEY (origem_insignia) REFERENCES insignia(id),
    CONSTRAINT origem_unica CHECK ( num_nonnulls(origem_sessao, origem_constelacao, origem_insignia) = 1 )
);

CREATE UNIQUE INDEX uq_ganho_xp_sessao
    ON ganho_xp (origem_sessao)
    WHERE origem_sessao IS NOT NULL;

CREATE UNIQUE INDEX uq_ganho_xp_insignia
    ON ganho_xp (usuario_id, origem_insignia)
    WHERE origem_insignia IS NOT NULL;

CREATE UNIQUE INDEX uq_ganho_xp_constelacao
    ON ganho_xp (usuario_id, origem_constelacao)
    WHERE origem_constelacao IS NOT NULL;