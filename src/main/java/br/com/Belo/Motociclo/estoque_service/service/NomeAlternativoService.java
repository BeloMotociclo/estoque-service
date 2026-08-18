package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.NomeAlternativoRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.NomeAlternativoResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.NomeAlternativo;
import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.repository.NomeAlternativoRepository;
import br.com.Belo.Motociclo.estoque_service.repository.PecaRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NomeAlternativoService {

    private final NomeAlternativoRepository repository;
    private final PecaRepository pecaRepository;
    private final LogAlteracaoService logService;

    public NomeAlternativoService(NomeAlternativoRepository repository,
                                  PecaRepository pecaRepository,
                                  LogAlteracaoService logService) {
        this.repository = repository;
        this.pecaRepository = pecaRepository;
        this.logService = logService;
    }

    public NomeAlternativoResponseDTO adicionar(UUID pecaId, NomeAlternativoRequestDTO dto) {
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        NomeAlternativo nome = new NomeAlternativo();
        nome.setPeca(peca);
        nome.setNome(dto.nome());
        NomeAlternativo salvo = repository.save(nome);
        logService.registrar("NomeAlternativo", salvo.getId().toString(), AcaoLog.CRIACAO,
                "Nome alternativo adicionado: " + salvo.getNome());
        return new NomeAlternativoResponseDTO(salvo.getId(), salvo.getNome());
    }

    public List<NomeAlternativoResponseDTO> listarPorPeca(UUID pecaId) {
        return repository.findByPecaId(pecaId)
                .stream()
                .map(n -> new NomeAlternativoResponseDTO(n.getId(), n.getNome()))
                .toList();
    }

    public @Nullable NomeAlternativoResponseDTO atualizar(Long id, NomeAlternativoRequestDTO dto) {
        NomeAlternativo nome = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nome alternativo não encontrado"));
        nome.setNome(dto.nome());
        NomeAlternativo salvo = repository.save(nome);
        logService.registrar("NomeAlternativo", salvo.getId().toString(), AcaoLog.EDICAO,
                "Nome alternativo atualizado: " + salvo.getNome());
        return new NomeAlternativoResponseDTO(salvo.getId(), salvo.getNome());
    }

    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Nome alternativo não encontrado");
        }
        repository.deleteById(id);
        logService.registrar("NomeAlternativo", id.toString(), AcaoLog.EXCLUSAO, "Nome alternativo removido");
    }
}