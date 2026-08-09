package br.com.Belo.Motociclo.estoque_service.dto;

import jakarta.validation.constraints.NotBlank;

public record ModeloRequestDTO(
        @NotBlank String nome
) {}

