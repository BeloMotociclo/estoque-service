CREATE TABLE foto_peca (
                           id BIGSERIAL PRIMARY KEY,
                           peca_id UUID NOT NULL REFERENCES peca(id),
                           url VARCHAR(500) NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT now(),
                           updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_foto_peca_peca_id ON foto_peca (peca_id);