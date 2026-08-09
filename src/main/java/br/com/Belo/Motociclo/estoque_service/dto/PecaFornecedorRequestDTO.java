package br.com.Belo.Motociclo.estoque_service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PecaFornecedorRequestDTO(
        @NotNull UUID fornecedorId
) {
}
