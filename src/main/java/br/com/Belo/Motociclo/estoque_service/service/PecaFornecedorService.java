package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.PecaFornecedorRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaFornecedorResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.Fornecedor;
import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import br.com.Belo.Motociclo.estoque_service.entity.PecaFornecedor;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.exception.RegraNegocioException;
import br.com.Belo.Motociclo.estoque_service.repository.FornecedorRepository;
import br.com.Belo.Motociclo.estoque_service.repository.PecaFornecedorRepository;
import br.com.Belo.Motociclo.estoque_service.repository.PecaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PecaFornecedorService {

    private final PecaFornecedorRepository pecaFornecedorRepository;
    private final PecaRepository pecaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final LogAlteracaoService logService;

    public PecaFornecedorService(PecaFornecedorRepository pecaFornecedorRepository,
                                 PecaRepository pecaRepository,
                                 FornecedorRepository fornecedorRepository,
                                 LogAlteracaoService logService) {
        this.pecaFornecedorRepository = pecaFornecedorRepository;
        this.pecaRepository = pecaRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.logService = logService;
    }

    public PecaFornecedorResponseDTO vincular(UUID pecaId, PecaFornecedorRequestDTO dto) {
        if (pecaFornecedorRepository.existsByPecaIdAndFornecedorId(pecaId, dto.fornecedorId())) {
            throw new RegraNegocioException("Fornecedor já vinculado a essa peça");
        }
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        Fornecedor fornecedor = fornecedorRepository.findById(dto.fornecedorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
        PecaFornecedor pf = new PecaFornecedor();
        pf.setPeca(peca);
        pf.setFornecedor(fornecedor);
        PecaFornecedor salvo = pecaFornecedorRepository.save(pf);
        logService.registrar("PecaFornecedor", salvo.getId().toString(), AcaoLog.CRIACAO,
                "Fornecedor vinculado: " + fornecedor.getNome());
        return new PecaFornecedorResponseDTO(salvo.getId(), fornecedor.getId(), fornecedor.getNome());
    }

    public List<PecaFornecedorResponseDTO> listarPorPeca(UUID pecaId) {
        return pecaFornecedorRepository.findByPecaId(pecaId)
                .stream()
                .map(pf -> new PecaFornecedorResponseDTO(pf.getId(), pf.getFornecedor().getId(), pf.getFornecedor().getNome()))
                .toList();
    }

    public void desvincular(Long id) {
        if (!pecaFornecedorRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Vínculo não encontrado");
        }
        pecaFornecedorRepository.deleteById(id);
        logService.registrar("PecaFornecedor", id.toString(), AcaoLog.EXCLUSAO, "Fornecedor desvinculado");
    }
}
