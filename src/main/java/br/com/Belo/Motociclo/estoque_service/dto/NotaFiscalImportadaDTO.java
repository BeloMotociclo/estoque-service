package br.com.Belo.Motociclo.estoque_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record NotaFiscalImportadaDTO(
        String numero,
        String chaveAcesso,
        String cnpjFornecedor,
        BigDecimal valorTotal,
        LocalDate data,
        List<ItemNotaFiscalDTO> itens
) {
}
