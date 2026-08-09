package br.com.Belo.Motociclo.estoque_service.controller;

import br.com.Belo.Motociclo.estoque_service.dto.PecaFornecedorRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaFornecedorResponseDTO;
import br.com.Belo.Motociclo.estoque_service.service.PecaFornecedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pecas/{pecaId}/fornecedores")
public class PecaFornecedorController {

    private final PecaFornecedorService service;

    public PecaFornecedorController(PecaFornecedorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PecaFornecedorResponseDTO> vincular(
            @PathVariable UUID pecaId,
            @Valid @RequestBody PecaFornecedorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.vincular(pecaId, dto));
    }

    @GetMapping
    public ResponseEntity<List<PecaFornecedorResponseDTO>> listar(@PathVariable UUID pecaId) {
        return ResponseEntity.ok(service.listarPorPeca(pecaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desvincular(@PathVariable Long id) {
        service.desvincular(id);
        return ResponseEntity.noContent().build();
    }
}
