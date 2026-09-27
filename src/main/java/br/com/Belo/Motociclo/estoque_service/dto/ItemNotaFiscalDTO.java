package br.com.Belo.Motociclo.estoque_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ItemNotaFiscalDTO(
        @NotBlank String codigoPeca,
        @NotNull @Positive BigDecimal precoUnitario,
        @NotNull @Positive Integer quantidade
) {
}