package br.com.Belo.Motociclo.estoque_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record NotaFiscalManualRequestDTO(
        @NotNull UUID fornecedorId,
        @NotBlank String numero,
        String chaveAcesso,
        @NotNull @Positive BigDecimal valorTotal,
        @NotNull LocalDate data,
        @NotEmpty List<@Valid ItemNotaFiscalDTO> itens
) {
}