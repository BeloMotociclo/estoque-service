package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.ModeloRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ModeloResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.Modelo;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.exception.RegraNegocioException;
import br.com.Belo.Motociclo.estoque_service.repository.ModeloRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeloService {

    private final ModeloRepository repository;
    private final LogAlteracaoService logService;

    public ModeloService(ModeloRepository repository, LogAlteracaoService logService) {
        this.repository = repository;
        this.logService = logService;
    }

    public ModeloResponseDTO criar(ModeloRequestDTO dto) {
        if (repository.existsByNome(dto.nome())) {
            throw new RegraNegocioException("Modelo já cadastrado");
        }
        Modelo modelo = new Modelo();
        modelo.setNome(dto.nome());
        Modelo salvo = repository.save(modelo);
        logService.registrar("Modelo", salvo.getId().toString(), AcaoLog.CRIACAO,
                "Modelo criado: " + salvo.getNome());
        return new ModeloResponseDTO(salvo.getId(), salvo.getNome());
    }

    public List<ModeloResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(m -> new ModeloResponseDTO(m.getId(), m.getNome()))
                .toList();
    }

    public ModeloResponseDTO atualizar(Long id, ModeloRequestDTO dto) {
        Modelo modelo = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Modelo não encontrado"));
        modelo.setNome(dto.nome());
        Modelo salvo = repository.save(modelo);
        logService.registrar("Modelo", salvo.getId().toString(), AcaoLog.EDICAO,
                "Modelo atualizado: " + salvo.getNome());
        return new ModeloResponseDTO(salvo.getId(), salvo.getNome());
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Modelo não encontrado");
        }
        repository.deleteById(id);
        logService.registrar("Modelo", id.toString(), AcaoLog.EXCLUSAO, "Modelo removido");
    }
}