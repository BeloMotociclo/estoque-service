CREATE TABLE fornecedor (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            nome VARCHAR(150) NOT NULL,
                            cnpj VARCHAR(18) NOT NULL UNIQUE,
                            endereco VARCHAR(255),
                            logo_url VARCHAR(500),
                            ativo BOOLEAN NOT NULL DEFAULT true,
                            created_at TIMESTAMP NOT NULL DEFAULT now(),
                            updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE contato (
                         id BIGSERIAL PRIMARY KEY,
                         fornecedor_id UUID NOT NULL REFERENCES fornecedor(id),
                         nome VARCHAR(150) NOT NULL,
                         telefone VARCHAR(20),
                         email VARCHAR(150),
                         cargo VARCHAR(100),
                         created_at TIMESTAMP NOT NULL DEFAULT now(),
                         updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE peca_fornecedor (
                                 id BIGSERIAL PRIMARY KEY,
                                 peca_id UUID NOT NULL REFERENCES peca(id),
                                 fornecedor_id UUID NOT NULL REFERENCES fornecedor(id),
                                 created_at TIMESTAMP NOT NULL DEFAULT now(),
                                 updated_at TIMESTAMP NOT NULL DEFAULT now(),
                                 UNIQUE(peca_id, fornecedor_id)
);