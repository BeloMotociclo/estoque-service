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

import java.io.IOException;
import java.io.InputStream;
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
        validarImagem(arquivo);
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

    private void validarImagem(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Selecione um arquivo de imagem.");
        }
        byte[] bytes;
        try {
            bytes = arquivo.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("Não foi possível ler o arquivo enviado.");
        }
        if (!isImagem(bytes)) {
            throw new IllegalArgumentException(
                    "Envie um arquivo de imagem válido (JPG, PNG, WebP ou GIF).");
        }
    }

    private boolean isImagem(byte[] b) {
        if (b == null || b.length < 4) {
            return false;
        }
        int b0 = b[0] & 0xFF;
        int b1 = b[1] & 0xFF;
        int b2 = b[2] & 0xFF;
        int b3 = b[3] & 0xFF;
        if (b0 == 0xFF && b1 == 0xD8 && b2 == 0xFF) {
            return true;
        }
        if (b0 == 0x89 && b1 == 0x50 && b2 == 0x4E && b3 == 0x47) {
            return true;
        }
        if (b0 == 'G' && b1 == 'I' && b2 == 'F' && b3 == '8') {
            return true;
        }
        if (b.length >= 12 && b0 == 'R' && b1 == 'I' && b2 == 'F' && b3 == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return true;
        }
        return false;
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

    public InputStream download(Long id) {
        FotoPeca foto = fotoPecaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Foto não encontrada"));
        return minioService.download(foto.getUrl());
    }
}