package br.com.Belo.Motociclo.estoque_service.dto;

import jakarta.validation.constraints.NotBlank;

public record FornecedorRequestDTO(
        @NotBlank String nome,
        @NotBlank String cnpj,
        String endereco
) {}

