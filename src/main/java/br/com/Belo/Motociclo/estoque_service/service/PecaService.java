package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.PecaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.mapper.PecaMapper;
import br.com.Belo.Motociclo.estoque_service.repository.PecaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PecaService {

    private final PecaRepository repository;
    private final PecaMapper mapper;

    public PecaService(PecaRepository repository, PecaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public PecaResponseDTO criar(PecaRequestDTO dto) {
        Peca peca = mapper.toEntity(dto);
        return mapper.toResponseDTO(repository.save(peca));
    }

    public PecaResponseDTO buscarPorId(UUID id) {
        Peca peca = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        return mapper.toResponseDTO(peca);
    }

    public Page<PecaResponseDTO> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable).map(mapper::toResponseDTO);
    }

    public PecaResponseDTO atualizar(UUID id, PecaRequestDTO dto) {
        Peca peca = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        peca.setCodigo(dto.codigo());
        peca.setQuantidade(dto.quantidade());
        peca.setCategoria(dto.categoria());
        peca.setMarca(dto.marca());
        peca.setPrecoVenda(dto.precoVenda());
        return mapper.toResponseDTO(repository.save(peca));
    }

    public void deletar(UUID id) {
        Peca peca = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        peca.setAtivo(false); // soft delete — nunca DELETE físico
        repository.save(peca);
    }
}