package br.com.Belo.Motociclo.estoque_service.controller;

import br.com.Belo.Motociclo.estoque_service.dto.PecaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaServentiaDTO;
import br.com.Belo.Motociclo.estoque_service.repository.PecaRepository;
import br.com.Belo.Motociclo.estoque_service.service.PecaService;
import br.com.Belo.Motociclo.estoque_service.service.ServentiaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pecas")
public class PecaController {

    private final PecaService service;
    private final PecaRepository repository;
    private final ServentiaService serventiaService;

    public PecaController(PecaService service, PecaRepository repository, ServentiaService serventiaService) {
        this.service = service;
        this.repository = repository;
        this.serventiaService = serventiaService;
    }

    @GetMapping("/total")
    public ResponseEntity<Long> total() {
        return ResponseEntity.ok(repository.countByAtivoTrue());
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> categorias() {
        return ResponseEntity.ok(service.listarCategorias());
    }

    @GetMapping("/serventias")
    public ResponseEntity<List<PecaServentiaDTO>> serventias() {
        return ResponseEntity.ok(serventiaService.listarTodas());
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
    public ResponseEntity<Page<PecaResponseDTO>> listar(
            Pageable pageable,
            @RequestParam(value = "incluirInativas", defaultValue = "false") boolean incluirInativas) {
        return ResponseEntity.ok(service.listar(pageable, incluirInativas));
    }

    @PostMapping("/{id}/reativar")
    public ResponseEntity<PecaResponseDTO> reativar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.reativar(id));
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