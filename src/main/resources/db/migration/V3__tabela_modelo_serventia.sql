CREATE TABLE modelo (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(100) NOT NULL UNIQUE,
                        created_at TIMESTAMP NOT NULL DEFAULT now(),
                        updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE serventia (
                           id BIGSERIAL PRIMARY KEY,
                           peca_id UUID NOT NULL REFERENCES peca(id),
                           modelo_id BIGINT NOT NULL REFERENCES modelo(id),
                           created_at TIMESTAMP NOT NULL DEFAULT now(),
                           updated_at TIMESTAMP NOT NULL DEFAULT now(),
                           UNIQUE(peca_id, modelo_id)
);