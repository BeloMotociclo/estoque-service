package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.PecaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.mapper.PecaMapper;
import br.com.Belo.Motociclo.estoque_service.repository.PecaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PecaService {

    private final PecaRepository repository;
    private final PecaMapper mapper;
    private final LogAlteracaoService logService;

    public PecaService(PecaRepository repository, PecaMapper mapper, LogAlteracaoService logService) {
        this.repository = repository;
        this.mapper = mapper;
        this.logService = logService;
    }

public PecaResponseDTO criar(PecaRequestDTO dto) {
        Peca peca = mapper.toEntity(dto);
        peca.setCategoria(dto.categoria().trim().toUpperCase());
        Peca salvo = repository.save(peca);
        logService.registrar("Peca", salvo.getId().toString(), AcaoLog.CRIACAO,
                "Pe��a criada: " + salvo.getCodigo());
        return mapper.toResponseDTO(salvo);
    }

    public PecaResponseDTO buscarPorId(UUID id) {
        Peca peca = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        return mapper.toResponseDTO(peca);
    }

    public Page<PecaResponseDTO> listar(Pageable pageable, boolean incluirInativas, String q) {
        Page<Peca> page;
        if (q != null && !q.isBlank()) {
            page = repository.buscar(q.trim(), incluirInativas, pageable);
        } else {
            page = incluirInativas
                    ? repository.findAll(pageable)
                    : repository.findAllByAtivoTrue(pageable);
        }
        return page.map(mapper::toResponseDTO);
    }

    public List<String> listarCategorias() {
        return repository.findAllCategorias();
    }

    public PecaResponseDTO atualizar(UUID id, PecaRequestDTO dto) {
        Peca peca = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        peca.setCodigo(dto.codigo());
        peca.setNome(dto.nome());
        peca.setQuantidade(dto.quantidade());
        peca.setCategoria(dto.categoria().trim().toUpperCase());
        peca.setMarca(dto.marca());
        peca.setPrecoVenda(dto.precoVenda());
        peca.setAtivo(true); // editar uma peça inativa reativa
        Peca salvo = repository.save(peca);
        logService.registrar("Peca", salvo.getId().toString(), AcaoLog.EDICAO,
                "Peça atualizada: " + salvo.getCodigo());
        return mapper.toResponseDTO(salvo);
    }

    public PecaResponseDTO reativar(UUID id) {
        Peca peca = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        peca.setAtivo(true);
        Peca salvo = repository.save(peca);
        logService.registrar("Peca", id.toString(), AcaoLog.EDICAO,
                "Peça reativada: " + salvo.getCodigo());
        return mapper.toResponseDTO(salvo);
    }

    public void deletar(UUID id) {
        Peca peca = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        peca.setAtivo(false); // soft delete — nunca DELETE físico
        repository.save(peca);
        logService.registrar("Peca", id.toString(), AcaoLog.EXCLUSAO,
                "Peça desativada: " + peca.getCodigo());
    }
}