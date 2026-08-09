package br.com.Belo.Motociclo.estoque_service.dto;

import java.util.UUID;

public record PecaFornecedorResponseDTO(
        Long id,
        UUID fornecedorId,
        String fornecedorNome
) {
}
