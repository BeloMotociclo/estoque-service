package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PecaRepository extends JpaRepository<Peca, UUID> {
    Optional<Peca> findByCodigoAndAtivoTrue(String codigo);
    Page<Peca> findAllByAtivoTrue(Pageable pageable);
    long countByAtivoTrue();

    @Query("SELECT DISTINCT UPPER(p.categoria) FROM Peca p WHERE p.categoria IS NOT NULL ORDER BY 1")
    List<String> findAllCategorias();
}