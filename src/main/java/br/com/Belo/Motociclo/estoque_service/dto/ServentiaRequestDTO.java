package br.com.Belo.Motociclo.estoque_service.dto;

import jakarta.validation.constraints.NotNull;

public record ServentiaRequestDTO(
        @NotNull Long modeloId
) {}
