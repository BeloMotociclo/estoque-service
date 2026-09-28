package br.com.Belo.Motociclo.estoque_service.dto;

import java.util.UUID;

public record PecaCriadaDTO(
        UUID id,
        String codigo,
        String nome
) {}
