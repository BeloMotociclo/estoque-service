CREATE EXTENSION IF NOT EXISTS pg_trgm;

ALTER TABLE peca ADD COLUMN nome VARCHAR(255);

UPDATE peca SET nome = codigo WHERE nome IS NULL;

ALTER TABLE peca ALTER COLUMN nome SET NOT NULL;

CREATE INDEX idx_peca_nome_trgm ON peca USING GIN (nome gin_trgm_ops);