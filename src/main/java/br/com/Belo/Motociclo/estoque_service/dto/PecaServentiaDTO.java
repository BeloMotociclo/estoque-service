package br.com.Belo.Motociclo.estoque_service.dto;

import java.util.UUID;

public record PecaServentiaDTO(
        UUID pecaId,
        Long serventiaId,
        Long modeloId,
        String modeloNome,
        String categoria
) {}