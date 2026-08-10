package br.com.Belo.Motociclo.estoque_service.dto;

import java.math.BigDecimal;

// DTO interno usado na importação do XML
public record ItemNotaFiscalDTO(
        String codigoPeca,
        BigDecimal precoUnitario,
        Integer quantidade
) {
}
