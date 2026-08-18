package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.FotoPeca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FotoPecaRepository extends JpaRepository<FotoPeca, Long> {
    List<FotoPeca> findByPecaId(UUID pecaId);
}