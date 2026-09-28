package br.com.Belo.Motociclo.estoque_service.dto;

import br.com.Belo.Motociclo.estoque_service.entity.TipoCodigoPeca;

import java.util.UUID;

public record CodigoPecaResponseDTO(
        Long id,
        TipoCodigoPeca tipo,
        UUID fornecedorId,
        String fornecedorNome,
        String codigo,
        String descricao
) {}
