package br.com.Belo.Motociclo.estoque_service.controller;

import br.com.Belo.Motociclo.estoque_service.dto.ServentiaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ServentiaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.service.ServentiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pecas/{pecaId}/serventia")
public class ServentiaController {

    private final ServentiaService service;

    public ServentiaController(ServentiaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ServentiaResponseDTO> vincular(
            @PathVariable UUID pecaId,
            @Valid @RequestBody ServentiaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.vincular(pecaId, dto));
    }

    @GetMapping
    public ResponseEntity<List<ServentiaResponseDTO>> listar(@PathVariable UUID pecaId) {
        return ResponseEntity.ok(service.listarPorPeca(pecaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desvincular(@PathVariable Long id) {
        service.desvincular(id);
        return ResponseEntity.noContent().build();
    }
}