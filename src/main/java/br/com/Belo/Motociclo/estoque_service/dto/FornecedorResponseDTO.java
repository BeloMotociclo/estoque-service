package br.com.Belo.Motociclo.estoque_service.dto;

import java.util.UUID;

public record FornecedorResponseDTO(
        UUID id,
        String nome,
        String cnpj,
        String endereco,
        String logoUrl
) {
}
