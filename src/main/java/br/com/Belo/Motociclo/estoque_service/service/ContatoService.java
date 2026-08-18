package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.ContatoRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ContatoResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.Contato;
import br.com.Belo.Motociclo.estoque_service.entity.Fornecedor;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.repository.ContatoRepository;
import br.com.Belo.Motociclo.estoque_service.repository.FornecedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final LogAlteracaoService logService;

    public ContatoService(ContatoRepository contatoRepository,
                          FornecedorRepository fornecedorRepository,
                          LogAlteracaoService logService) {
        this.contatoRepository = contatoRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.logService = logService;
    }

    public ContatoResponseDTO adicionar(UUID fornecedorId, ContatoRequestDTO dto) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
        Contato c = new Contato();
        c.setFornecedor(fornecedor);
        c.setNome(dto.nome());
        c.setTelefone(dto.telefone());
        c.setEmail(dto.email());
        c.setCargo(dto.cargo());
        Contato salvo = contatoRepository.save(c);
        logService.registrar("Contato", salvo.getId().toString(), AcaoLog.CRIACAO,
                "Contato adicionado: " + salvo.getNome());
        return toDTO(salvo);
    }

    public List<ContatoResponseDTO> listarPorFornecedor(UUID fornecedorId) {
        return contatoRepository.findByFornecedorId(fornecedorId)
                .stream().map(this::toDTO).toList();
    }

    public ContatoResponseDTO atualizar(Long id, ContatoRequestDTO dto) {
        Contato c = contatoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contato não encontrado"));
        c.setNome(dto.nome());
        c.setTelefone(dto.telefone());
        c.setEmail(dto.email());
        c.setCargo(dto.cargo());
        Contato salvo = contatoRepository.save(c);
        logService.registrar("Contato", salvo.getId().toString(), AcaoLog.EDICAO,
                "Contato atualizado: " + salvo.getNome());
        return toDTO(salvo);
    }

    public void remover(Long id) {
        if (!contatoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Contato não encontrado");
        }
        contatoRepository.deleteById(id);
        logService.registrar("Contato", id.toString(), AcaoLog.EXCLUSAO, "Contato removido");
    }

    private ContatoResponseDTO toDTO(Contato c) {
        return new ContatoResponseDTO(c.getId(), c.getNome(), c.getTelefone(), c.getEmail(), c.getCargo());
    }
}
