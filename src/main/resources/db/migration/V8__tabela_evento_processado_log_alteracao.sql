CREATE TABLE evento_processado (
    id BIGSERIAL PRIMARY KEY,
    evento_id UUID NOT NULL UNIQUE,
    processado_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE log_alteracao (
    id BIGSERIAL PRIMARY KEY,
    usuario_id UUID,
    entidade VARCHAR(100) NOT NULL,
    entidade_id VARCHAR(100) NOT NULL,
    acao VARCHAR(20) NOT NULL,
    detalhes TEXT,
    data TIMESTAMP NOT NULL DEFAULT now()
);