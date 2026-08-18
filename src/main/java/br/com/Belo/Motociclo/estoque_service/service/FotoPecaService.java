package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.FotoPecaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.FotoPeca;
import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.repository.FotoPecaRepository;
import br.com.Belo.Motociclo.estoque_service.repository.PecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class FotoPecaService {

    private final FotoPecaRepository fotoPecaRepository;
    private final PecaRepository pecaRepository;
    private final MinioService minioService;
    private final LogAlteracaoService logService;

    public FotoPecaService(FotoPecaRepository fotoPecaRepository,
                           PecaRepository pecaRepository,
                           MinioService minioService,
                           LogAlteracaoService logService) {
        this.fotoPecaRepository = fotoPecaRepository;
        this.pecaRepository = pecaRepository;
        this.minioService = minioService;
        this.logService = logService;
    }

    public FotoPecaResponseDTO upload(UUID pecaId, MultipartFile arquivo) {
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada"));
        String url = minioService.upload(arquivo, "pecas");
        FotoPeca foto = new FotoPeca();
        foto.setPeca(peca);
        foto.setUrl(url);
        FotoPeca salvo = fotoPecaRepository.save(foto);
        logService.registrar("FotoPeca", salvo.getId().toString(), AcaoLog.CRIACAO, "Foto adicionada");
        return new FotoPecaResponseDTO(salvo.getId(), salvo.getUrl());
    }

    public List<FotoPecaResponseDTO> listar(UUID pecaId) {
        return fotoPecaRepository.findByPecaId(pecaId)
                .stream()
                .map(f -> new FotoPecaResponseDTO(f.getId(), f.getUrl()))
                .toList();
    }

    public void deletar(Long id) {
        FotoPeca foto = fotoPecaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Foto não encontrada"));
        minioService.deletar(foto.getUrl());
        fotoPecaRepository.deleteById(id);
        logService.registrar("FotoPeca", id.toString(), AcaoLog.EXCLUSAO, "Foto removida");
    }
}