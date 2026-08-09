package br.com.Belo.Motociclo.estoque_service.controller;

import br.com.Belo.Motociclo.estoque_service.dto.ContatoRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ContatoResponseDTO;
import br.com.Belo.Motociclo.estoque_service.service.ContatoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/fornecedores/{fornecedorId}/contatos")
public class ContatoController {

    private final ContatoService service;

    public ContatoController(ContatoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContatoResponseDTO> adicionar(
            @PathVariable UUID fornecedorId,
            @Valid @RequestBody ContatoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionar(fornecedorId, dto));
    }

    @GetMapping
    public ResponseEntity<List<ContatoResponseDTO>> listar(@PathVariable UUID fornecedorId) {
        return ResponseEntity.ok(service.listarPorFornecedor(fornecedorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContatoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ContatoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
