package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.FornecedorRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.FornecedorResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.Fornecedor;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.repository.FornecedorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;

    public FornecedorService(FornecedorRepository repository) {
        this.repository = repository;
    }

    public FornecedorResponseDTO criar(FornecedorRequestDTO dto) {
        if (repository.findByCnpjAndAtivoTrue(dto.cnpj()).isPresent()) {
            throw new RuntimeException("Fornecedor com esse CNPJ já cadastrado");
        }
        Fornecedor f = new Fornecedor();
        f.setNome(dto.nome());
        f.setCnpj(dto.cnpj());
        f.setEndereco(dto.endereco());
        return toDTO(repository.save(f));
    }

    public Page<FornecedorResponseDTO> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable).map(this::toDTO);
    }

    public FornecedorResponseDTO buscarPorId(UUID id) {
        return toDTO(repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado")));
    }

    public FornecedorResponseDTO atualizar(UUID id, FornecedorRequestDTO dto) {
        Fornecedor f = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
        f.setNome(dto.nome());
        f.setCnpj(dto.cnpj());
        f.setEndereco(dto.endereco());
        return toDTO(repository.save(f));
    }

    public void deletar(UUID id) {
        Fornecedor f = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
        f.setAtivo(false);
        repository.save(f);
    }

    private FornecedorResponseDTO toDTO(Fornecedor f) {
        return new FornecedorResponseDTO(f.getId(), f.getNome(), f.getCnpj(), f.getEndereco(), f.getLogoUrl());
    }
}

