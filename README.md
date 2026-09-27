# estoque-service

Catálogo e entradas: peças, fotos, modelos, fornecedores e notas fiscais. Porta 8081 · Spring MVC + JPA + MinIO + Flyway.

## Rodando

- Stack completa: ver README do repo infra
- Isolado (IDE): `docker compose up -d db-estoque rabbitmq minio` no compose da raiz e rodar a aplicação (defaults em localhost)

## Variáveis de ambiente

| Variável | Default local | Para que serve |
|---|---|---|
| ESTOQUE_DB_URL / ESTOQUE_DB_USERNAME / ESTOQUE_DB_PASSWORD | localhost:5432 / estoque_user / estoque_pass | Banco de dados |
| RABBITMQ_HOST / PORT / USERNAME / PASSWORD | localhost / 5672 / admin / admin123 | Mensageria (config presente; eventos ainda não usados) |
| MINIO_URL / ACCESS_KEY / SECRET_KEY / BUCKET | http://localhost:9000 / minioadmin / minioadmin / oficina | Fotos de peças |
| JWT_SECRET / JWT_EXPIRACAO | (placeholder) / 86400000 | Validação do token |

## Endpoints principais

- /v1/pecas (+ /{id}/reativar, /total, /categorias, /serventias, fotos em /{id}/fotos)
- /v1/modelos (+ vínculo de peças) · /v1/fornecedores (+ contatos)
- /v1/notas-fiscais (+ /importar XML; consulta SEFAZ responde 503 sem certificado A1)
- /v1/notas-fiscais/pecas/{id}/historico-precos

## Notas

- Alteração de schema só via Flyway (`ddl-auto: validate`)
- Fotos ficam no MinIO (bucket privado) e são servidas por endpoints de blob
