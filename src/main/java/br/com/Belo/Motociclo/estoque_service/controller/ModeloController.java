package br.com.Belo.Motociclo.estoque_service.controller;

import br.com.Belo.Motociclo.estoque_service.dto.ModeloRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ModeloResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaDoModeloResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaIdRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ServentiaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.service.ModeloService;
import br.com.Belo.Motociclo.estoque_service.service.ServentiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/modelos")
public class ModeloController {

    private final ModeloService service;
    private final ServentiaService serventiaService;

    public ModeloController(ModeloService service, ServentiaService serventiaService) {
        this.service = service;
        this.serventiaService = serventiaService;
    }

    @PostMapping
    public ResponseEntity<ModeloResponseDTO> criar(@Valid @RequestBody ModeloRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ModeloResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModeloResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ModeloRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @GetMapping("/{id}/pecas")
    public ResponseEntity<List<PecaDoModeloResponseDTO>> listarPecas(@PathVariable Long id) {
        return ResponseEntity.ok(serventiaService.listarPecasPorModelo(id));
    }

    @PostMapping("/{id}/pecas")
    public ResponseEntity<ServentiaResponseDTO> vincularPeca(
            @PathVariable Long id,
            @Valid @RequestBody PecaIdRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serventiaService.vincularPeca(id, dto.pecaId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}