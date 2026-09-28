package br.com.Belo.Motociclo.estoque_service.dto;

import br.com.Belo.Motociclo.estoque_service.entity.ResolucaoPendente;
import br.com.Belo.Motociclo.estoque_service.entity.StatusItemPendente;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemNotaPendenteResponseDTO(
        Long id,
        String codigo,
        String descricao,
        Integer quantidade,
        BigDecimal precoUnitario,
        StatusItemPendente status,
        ResolucaoPendente resolucao,
        UUID pecaId,
        String pecaCodigo,
        String pecaNome
) {}
