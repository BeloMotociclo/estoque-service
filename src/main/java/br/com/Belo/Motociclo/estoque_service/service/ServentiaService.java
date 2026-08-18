package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.ServentiaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ServentiaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.Modelo;
import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import br.com.Belo.Motociclo.estoque_service.entity.Serventia;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.exception.RegraNegocioException;
import br.com.Belo.Motociclo.estoque_service.repository.ModeloRepository;
import br.com.Belo.Motociclo.estoque_service.repository.PecaRepository;
import br.com.Belo.Motociclo.estoque_service.repository.ServentiaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ServentiaService {

    private final ServentiaRepository serventiaRepository;
    private final PecaRepository pecaRepository;
    private final ModeloRepository modeloRepository;
    private final LogAlteracaoService logService;

    public ServentiaService(ServentiaRepository serventiaRepository,
                            PecaRepository pecaRepository,
                            ModeloRepository modeloRepository,
                            LogAlteracaoService logService) {
        this.serventiaRepository = serventiaRepository;
        this.pecaRepository = pecaRepository;
        this.modeloRepository = modeloRepository;
        this.logService = logService;
    }

    public ServentiaResponseDTO vincular(UUID pecaId, ServentiaRequestDTO dto) {
        if (serventiaRepository.existsByPecaIdAndModeloId(pecaId, dto.modeloId())) {
            throw new RegraNegocioException("Esse modelo já está vinculado a essa peça");
        }
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        Modelo modelo = modeloRepository.findById(dto.modeloId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Modelo não encontrado"));
        Serventia serventia = new Serventia();
        serventia.setPeca(peca);
        serventia.setModelo(modelo);
        Serventia salvo = serventiaRepository.save(serventia);
        logService.registrar("Serventia", salvo.getId().toString(), AcaoLog.CRIACAO,
                "Serventia vinculada: " + modelo.getNome());
        return new ServentiaResponseDTO(salvo.getId(), modelo.getId(), modelo.getNome());
    }

    public List<ServentiaResponseDTO> listarPorPeca(UUID pecaId) {
        return serventiaRepository.findByPecaId(pecaId)
                .stream()
                .map(s -> new ServentiaResponseDTO(s.getId(), s.getModelo().getId(), s.getModelo().getNome()))
                .toList();
    }

    public void desvincular(Long id) {
        if (!serventiaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Vínculo não encontrado");
        }
        serventiaRepository.deleteById(id);
        logService.registrar("Serventia", id.toString(), AcaoLog.EXCLUSAO, "Serventia desvinculada");
    }
}