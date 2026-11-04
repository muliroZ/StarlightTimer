CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    criado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    excluido_em TIMESTAMPTZ NULL,

    CONSTRAINT status_check CHECK ( status IN ('ACTIVE', 'DISABLED') )
);

CREATE TABLE identidade (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    provedor VARCHAR(20),
    id_externo TEXT,
    senha_hash TEXT,

    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT unique_provedor_id_externo UNIQUE (provedor, id_externo)
);

CREATE TABLE perfil (
    usuario_id UUID UNIQUE NOT NULL,
    nome_exibido VARCHAR(50) NOT NULL,
    avatar TEXT,
    bio TEXT,
    fuso_horario VARCHAR(40) NOT NULL,
    xp_total INTEGER DEFAULT 0,

    CONSTRAINT pk_perfil_usuario PRIMARY KEY (usuario_id),
    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE configuracoes_usuario (
    usuario_id UUID UNIQUE NOT NULL,
    foco_em_segundos INTEGER DEFAULT 1500,
    pausa_em_segundos INTEGER DEFAULT 300,
    iniciar_pausa_auto BOOLEAN DEFAULT TRUE,
    som_habilitado BOOLEAN DEFAULT TRUE,

    CONSTRAINT pk_configuracoes_usuario PRIMARY KEY (usuario_id),
    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT foco_intervalo_check CHECK ( (foco_em_segundos % 300 = 0) AND (foco_em_segundos BETWEEN 900 AND 5400) ),
    CONSTRAINT pausa_intervalo_check CHECK ( pausa_em_segundos BETWEEN 180 AND 900 )
);

CREATE TABLE refresh_token (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    token_hash TEXT NOT NULL,
    expira_em TIMESTAMPTZ NOT NULL,
    criado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    revogado_em TIMESTAMPTZ NULL,
    user_agent TEXT NULL,

    CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);