package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.CodigoPecaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.CodigoPecaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.CodigoPeca;
import br.com.Belo.Motociclo.estoque_service.entity.Fornecedor;
import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import br.com.Belo.Motociclo.estoque_service.entity.TipoCodigoPeca;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.exception.RegraNegocioException;
import br.com.Belo.Motociclo.estoque_service.repository.CodigoPecaRepository;
import br.com.Belo.Motociclo.estoque_service.repository.FornecedorRepository;
import br.com.Belo.Motociclo.estoque_service.repository.PecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CodigoPecaService {

    private final CodigoPecaRepository codigoPecaRepository;
    private final PecaRepository pecaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final LogAlteracaoService logService;

    public CodigoPecaService(CodigoPecaRepository codigoPecaRepository,
                             PecaRepository pecaRepository,
                             FornecedorRepository fornecedorRepository,
                             LogAlteracaoService logService) {
        this.codigoPecaRepository = codigoPecaRepository;
        this.pecaRepository = pecaRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.logService = logService;
    }

    @Transactional(readOnly = true)
    public List<CodigoPecaResponseDTO> listar(UUID pecaId) {
        return codigoPecaRepository.findByPecaIdOrderByTipoAscCodigoAsc(pecaId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public CodigoPecaResponseDTO adicionar(UUID pecaId, CodigoPecaRequestDTO dto) {
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));

        String codigo = dto.codigo().trim();
        Fornecedor fornecedor = null;

        if (dto.tipo() == TipoCodigoPeca.FORNECEDOR) {
            if (dto.fornecedorId() == null) {
                throw new RegraNegocioException("Código de fornecedor exige o fornecedorId");
            }
            fornecedor = fornecedorRepository.findByIdAndAtivoTrue(dto.fornecedorId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
            if (codigoPecaRepository.existsByTipoAndFornecedorIdAndCodigo(
                    TipoCodigoPeca.FORNECEDOR, fornecedor.getId(), codigo)) {
                throw new RegraNegocioException("Código " + codigo + " já cadastrado para este fornecedor");
            }
        } else if (codigoPecaRepository.existsByTipoAndCodigo(dto.tipo(), codigo)) {
            throw new RegraNegocioException("Código " + codigo + " já cadastrado");
        }

        CodigoPeca codigoPeca = new CodigoPeca();
        codigoPeca.setPeca(peca);
        codigoPeca.setTipo(dto.tipo());
        codigoPeca.setFornecedor(fornecedor);
        codigoPeca.setCodigo(codigo);
        codigoPeca.setDescricao(dto.descricao());
        CodigoPeca salvo = codigoPecaRepository.save(codigoPeca);

        logService.registrar("Peca", pecaId.toString(), AcaoLog.EDICAO,
                "Código " + dto.tipo() + " adicionado: " + codigo);
        return toDTO(salvo);
    }

    @Transactional
    public void remover(UUID pecaId, Long codigoId) {
        CodigoPeca codigoPeca = codigoPecaRepository.findByIdAndPecaId(codigoId, pecaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Código não encontrado para esta peça"));
        codigoPecaRepository.delete(codigoPeca);
        logService.registrar("Peca", pecaId.toString(), AcaoLog.EDICAO,
                "Código removido: " + codigoPeca.getCodigo());
    }

    private CodigoPecaResponseDTO toDTO(CodigoPeca c) {
        return new CodigoPecaResponseDTO(
                c.getId(), c.getTipo(),
                c.getFornecedor() != null ? c.getFornecedor().getId() : null,
                c.getFornecedor() != null ? c.getFornecedor().getNome() : null,
                c.getCodigo(), c.getDescricao());
    }
}
