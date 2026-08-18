package br.com.Belo.Motociclo.estoque_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PecaRequestDTO(
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotNull Integer quantidade,
        @NotBlank String categoria,
        String marca,
        @NotNull @Positive BigDecimal precoVenda
) {}
