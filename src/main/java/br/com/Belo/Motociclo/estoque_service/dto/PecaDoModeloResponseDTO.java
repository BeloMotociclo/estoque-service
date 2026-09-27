package br.com.Belo.Motociclo.estoque_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PecaDoModeloResponseDTO(
    Long serventiaId,
    UUID pecaId,
    String codigo,
    String nome,
    String categoria,
    String marca,
    BigDecimal precoVenda
) {}