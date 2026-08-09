package br.com.Belo.Motociclo.estoque_service.dto;

public record ContatoResponseDTO(
        Long id,
        String nome,
        String telefone,
        String email,
        String cargo
) {}
