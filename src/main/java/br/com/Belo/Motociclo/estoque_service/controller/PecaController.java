package br.com.Belo.Motociclo.estoque_service.controller;

import br.com.Belo.Motociclo.estoque_service.dto.PecaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.service.PecaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pecas")
public class PecaController {

    private final PecaService service;

    public PecaController(PecaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PecaResponseDTO> criar(@Valid @RequestBody PecaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<PecaResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }
    @PutMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody PecaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}