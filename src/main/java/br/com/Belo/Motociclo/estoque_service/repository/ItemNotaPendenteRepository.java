package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.ItemNotaPendente;
import br.com.Belo.Motociclo.estoque_service.entity.StatusItemPendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemNotaPendenteRepository extends JpaRepository<ItemNotaPendente, Long> {

    List<ItemNotaPendente> findByNotaFiscalIdOrderByIdAsc(UUID notaFiscalId);

    List<ItemNotaPendente> findByNotaFiscalIdAndStatusOrderByIdAsc(UUID notaFiscalId, StatusItemPendente status);
}
