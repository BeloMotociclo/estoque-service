package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.Fornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, UUID> {
    Optional<Fornecedor> findByCnpjAndAtivoTrue(String cnpj);
    Optional<Fornecedor> findByIdAndAtivoTrue(UUID id);
    Page<Fornecedor> findAllByAtivoTrue(Pageable pageable);
    long countByAtivoTrue();

    @Query(value = "SELECT * FROM fornecedor WHERE ativo = true "
            + "AND regexp_replace(cnpj, '\\D', '', 'g') = :cnpj LIMIT 1", nativeQuery = true)
    Optional<Fornecedor> buscarAtivoPorCnpjNormalizado(@Param("cnpj") String cnpj);
}
