package br.com.Belo.Motociclo.estoque_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConsultaNotaFiscalRequestDTO(
        @NotBlank
        @Size(min = 44, max = 44)
        @Pattern(regexp = "\\d{44}")
        String chaveAcesso
) {
}