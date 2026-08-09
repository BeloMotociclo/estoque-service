package br.com.Belo.Motociclo.estoque_service.controller;

import br.com.Belo.Motociclo.estoque_service.dto.NomeAlternativoRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.NomeAlternativoResponseDTO;
import br.com.Belo.Motociclo.estoque_service.service.NomeAlternativoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pecas/{pecaId}/nomes-alternativos")
public class NomeAlternativoController {

    private final NomeAlternativoService service;

    public NomeAlternativoController(NomeAlternativoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NomeAlternativoResponseDTO> adicionar(
            @PathVariable UUID pecaId,
            @Valid @RequestBody NomeAlternativoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionar(pecaId, dto));
    }

    @GetMapping
    public ResponseEntity<List<NomeAlternativoResponseDTO>> listar(@PathVariable UUID pecaId) {
        return ResponseEntity.ok(service.listarPorPeca(pecaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NomeAlternativoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody NomeAlternativoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}