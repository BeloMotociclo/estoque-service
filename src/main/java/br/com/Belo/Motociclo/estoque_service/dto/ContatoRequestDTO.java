package br.com.Belo.Motociclo.estoque_service.dto;

import jakarta.validation.constraints.NotBlank;

public record ContatoRequestDTO(
        @NotBlank String nome,
        String telefone,
        String email,
        String cargo
) {}
