package br.com.Belo.Motociclo.estoque_service.controller;

import br.com.Belo.Motociclo.estoque_service.dto.CadastrarPecaPendenciaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ConsultaNotaFiscalRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.HistoricoPrecoResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ItemNotaPendenteResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.NotaFiscalManualRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.NotaFiscalResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.VincularPendenciaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.service.NotaFiscalService;
import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<NotaFiscalResponseDTO> cadastrar(
            @Valid @RequestBody NotaFiscalManualRequestDTO request,
            @RequestParam(value = "criarPecasAutomaticamente", defaultValue = "false") boolean criarPecasAutomaticamente) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.cadastrarManualmente(request, criarPecasAutomaticamente));
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NotaFiscalResponseDTO> importar(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "criarPecasAutomaticamente", defaultValue = "false") boolean criarPecasAutomaticamente) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.importar(arquivo, criarPecasAutomaticamente));
    }

    @PostMapping("/consultar-sefaz")
    public ResponseEntity<Void> consultarSefaz(
            @Valid @RequestBody ConsultaNotaFiscalRequestDTO request) {
        service.consultarSefaz(request.chaveAcesso());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/itens-pendentes")
    public ResponseEntity<List<ItemNotaPendenteResponseDTO>> itensPendentes(@PathVariable UUID id) {
        return ResponseEntity.ok(service.listarItensPendentes(id));
    }

    @PostMapping("/itens-pendentes/{pendenteId}/vincular")
    public ResponseEntity<ItemNotaPendenteResponseDTO> vincularPendencia(
            @PathVariable Long pendenteId,
            @Valid @RequestBody VincularPendenciaRequestDTO request) {
        return ResponseEntity.ok(service.vincularPendencia(pendenteId, request.pecaId()));
    }

    @PostMapping("/itens-pendentes/{pendenteId}/cadastrar-peca")
    public ResponseEntity<ItemNotaPendenteResponseDTO> cadastrarPecaParaPendencia(
            @PathVariable Long pendenteId,
            @Valid @RequestBody CadastrarPecaPendenciaRequestDTO request) {
        return ResponseEntity.ok(service.cadastrarPecaParaPendencia(pendenteId, request));
    }

    @PostMapping("/itens-pendentes/{pendenteId}/descartar")
    public ResponseEntity<ItemNotaPendenteResponseDTO> descartarPendencia(@PathVariable Long pendenteId) {
        return ResponseEntity.ok(service.descartarPendencia(pendenteId));
    }

    @GetMapping
    public ResponseEntity<Page<NotaFiscalResponseDTO>> listar(
            @RequestParam(required = false) UUID fornecedorId,
            Pageable pageable) {
        return ResponseEntity.ok(service.listar(fornecedorId, pageable));
    }

    @GetMapping("/pecas/{pecaId}/historico-precos")
    public ResponseEntity<List<HistoricoPrecoResponseDTO>> historicoPorPeca(
            @PathVariable UUID pecaId) {
        return ResponseEntity.ok(service.historicoPrecosPorPeca(pecaId));
    }
}
