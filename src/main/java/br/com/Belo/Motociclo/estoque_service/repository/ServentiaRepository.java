package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.Serventia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServentiaRepository extends JpaRepository<Serventia, Long> {
    List<Serventia> findByPecaId(UUID pecaId);
    boolean existsByPecaIdAndModeloId(UUID pecaId, Long modeloId);
}