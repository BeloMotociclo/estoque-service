package br.com.Belo.Motociclo.estoque_service.dto;

import br.com.Belo.Motociclo.estoque_service.entity.TipoCodigoPeca;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CodigoPecaRequestDTO(
        @NotNull TipoCodigoPeca tipo,
        UUID fornecedorId,
        @NotBlank String codigo,
        String descricao
) {}
