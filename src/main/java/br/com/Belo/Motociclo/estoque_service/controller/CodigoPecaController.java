package br.com.Belo.Motociclo.estoque_service.controller;

import br.com.Belo.Motociclo.estoque_service.dto.CodigoPecaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.CodigoPecaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.service.CodigoPecaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pecas/{pecaId}/codigos")
public class CodigoPecaController {

    private final CodigoPecaService service;

    public CodigoPecaController(CodigoPecaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CodigoPecaResponseDTO>> listar(@PathVariable UUID pecaId) {
        return ResponseEntity.ok(service.listar(pecaId));
    }

    @PostMapping
    public ResponseEntity<CodigoPecaResponseDTO> adicionar(
            @PathVariable UUID pecaId,
            @Valid @RequestBody CodigoPecaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionar(pecaId, dto));
    }

    @DeleteMapping("/{codigoId}")
    public ResponseEntity<Void> remover(@PathVariable UUID pecaId, @PathVariable Long codigoId) {
        service.remover(pecaId, codigoId);
        return ResponseEntity.noContent().build();
    }
}
