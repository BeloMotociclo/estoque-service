package br.com.Belo.Motociclo.estoque_service.controller;

import br.com.Belo.Motociclo.estoque_service.dto.HistoricoPrecoResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.NotaFiscalResponseDTO;
import br.com.Belo.Motociclo.estoque_service.service.NotaFiscalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/notas-fiscais")
public class NotaFiscalController {

    private final NotaFiscalService service;

    public NotaFiscalController(NotaFiscalService service) {
        this.service = service;
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NotaFiscalResponseDTO> importar(
            @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.importar(arquivo));
    }

    @GetMapping
    public ResponseEntity<Page<NotaFiscalResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/pecas/{pecaId}/historico-precos")
    public ResponseEntity<List<HistoricoPrecoResponseDTO>> historicoPorPeca(
            @PathVariable UUID pecaId) {
        return ResponseEntity.ok(service.historicoPrecosPorPeca(pecaId));
    }
}