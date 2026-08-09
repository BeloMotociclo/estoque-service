package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.NomeAlternativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NomeAlternativoRepository extends JpaRepository<NomeAlternativo, Long> {
    List<NomeAlternativo> findByPecaId(UUID pecaId);
}