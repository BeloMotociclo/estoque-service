package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.HistoricoPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoricoPrecoRepository extends JpaRepository<HistoricoPreco, Long> {
    List<HistoricoPreco> findByPecaIdOrderByDataDesc(UUID pecaId);
    List<HistoricoPreco> findByNotaFiscalIdOrderByDataDesc(UUID notaFiscalId);
}
