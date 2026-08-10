package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.NotaFiscal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, UUID> {
    boolean existsByFornecedorIdAndNumero(UUID fornecedorId, String numero);
    boolean existsByChaveAcesso(String chaveAcesso);
    Page<NotaFiscal> findAllByAtivoTrue(Pageable pageable);
}

