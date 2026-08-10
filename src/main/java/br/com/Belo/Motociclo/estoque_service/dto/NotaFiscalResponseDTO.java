package br.com.Belo.Motociclo.estoque_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record NotaFiscalResponseDTO(
        UUID id,
        UUID fornecedorId,
        String fornecedorNome,
        String numero,
        String chaveAcesso,
        BigDecimal valorTotal,
        LocalDate data,
        List<HistoricoPrecoResponseDTO> itens
) {}

