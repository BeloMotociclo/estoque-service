package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.PecaFornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PecaFornecedorRepository extends JpaRepository<PecaFornecedor, Long> {
    List<PecaFornecedor> findByPecaId(UUID pecaId);
    boolean existsByPecaIdAndFornecedorId(UUID pecaId, UUID fornecedorId);
}
