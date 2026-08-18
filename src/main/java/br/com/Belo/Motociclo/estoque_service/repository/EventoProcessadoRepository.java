package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.EventoProcessado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventoProcessadoRepository extends JpaRepository<EventoProcessado, Long> {
    boolean existsByEventoId(UUID eventoId);
}