package br.com.Belo.Motociclo.estoque_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record HistoricoPrecoResponseDTO(
        Long id,
        UUID pecaId,
        String pecaCodigo,
        BigDecimal precoCompra,
        LocalDate data
) {
}
