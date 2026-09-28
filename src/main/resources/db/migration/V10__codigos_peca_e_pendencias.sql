CREATE TABLE codigo_peca (
    id BIGSERIAL PRIMARY KEY,
    peca_id UUID NOT NULL REFERENCES peca(id),
    tipo VARCHAR(20) NOT NULL,
    fornecedor_id UUID REFERENCES fornecedor(id),
    codigo VARCHAR(50) NOT NULL,
    descricao VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_codigo_peca_fornecedor ON codigo_peca (fornecedor_id, codigo) WHERE tipo = 'FORNECEDOR';
CREATE UNIQUE INDEX uq_codigo_peca_legado ON codigo_peca (codigo) WHERE tipo = 'SISTEMA_ANTIGO';
CREATE UNIQUE INDEX uq_codigo_peca_alternativo ON codigo_peca (codigo) WHERE tipo = 'ALTERNATIVO';
CREATE INDEX idx_codigo_peca_peca ON codigo_peca (peca_id);

CREATE TABLE item_nota_pendente (
    id BIGSERIAL PRIMARY KEY,
    nota_fiscal_id UUID NOT NULL REFERENCES nota_fiscal(id),
    codigo VARCHAR(50) NOT NULL,
    descricao VARCHAR(255),
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    resolucao VARCHAR(20),
    peca_id UUID REFERENCES peca(id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_item_nota_pendente_nota ON item_nota_pendente (nota_fiscal_id);
