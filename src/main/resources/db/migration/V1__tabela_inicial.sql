CREATE EXTENSION IF NOT EXISTS "pgcrypto";  -- pra gerar UUID
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE peca (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      codigo VARCHAR(50) NOT NULL UNIQUE,
                      quantidade INT NOT NULL DEFAULT 0,
                      categoria VARCHAR(100) NOT NULL,
                      marca VARCHAR(100),
                      preco_venda NUMERIC(10,2) NOT NULL,
                      version INT NOT NULL DEFAULT 0,
                      ativo BOOLEAN NOT NULL DEFAULT true,
                      created_at TIMESTAMP NOT NULL DEFAULT now(),
                      updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_peca_codigo_trgm ON peca USING GIN (codigo gin_trgm_ops);