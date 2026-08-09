CREATE TABLE nome_alternativo (
    id BIGSERIAL PRIMARY KEY,
    peca_id UUID NOT NULL REFERENCES peca(id),
    nome VARCHAR(150) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_nome_alternativo_trgm ON nome_alternativo USING GIN (nome gin_trgm_ops);