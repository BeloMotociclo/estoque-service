CREATE TABLE nota_fiscal (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             fornecedor_id UUID NOT NULL REFERENCES fornecedor(id),
                             numero VARCHAR(50) NOT NULL,
                             chave_acesso VARCHAR(44),
                             valor_total NUMERIC(10,2) NOT NULL,
                             data DATE NOT NULL,
                             ativo BOOLEAN NOT NULL DEFAULT true,
                             created_at TIMESTAMP NOT NULL DEFAULT now(),
                             updated_at TIMESTAMP NOT NULL DEFAULT now(),
                             UNIQUE(fornecedor_id, numero),
                             UNIQUE(chave_acesso)
);

CREATE TABLE historico_preco (
                                 id BIGSERIAL PRIMARY KEY,
                                 peca_id UUID NOT NULL REFERENCES peca(id),
                                 fornecedor_id UUID NOT NULL REFERENCES fornecedor(id),
                                 nota_fiscal_id UUID NOT NULL REFERENCES nota_fiscal(id),
                                 preco_compra NUMERIC(10,2) NOT NULL,
                                 data DATE NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT now(),
                                 updated_at TIMESTAMP NOT NULL DEFAULT now()
);