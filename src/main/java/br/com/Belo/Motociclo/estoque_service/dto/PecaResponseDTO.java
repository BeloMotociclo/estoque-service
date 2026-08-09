package br.com.Belo.Motociclo.estoque_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PecaResponseDTO(
        UUID id,
        String codigo,
        Integer quantidade,
        String categoria,
        String marca,
        BigDecimal precoVenda
) {}