package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.entity.EventoProcessado;
import br.com.Belo.Motociclo.estoque_service.repository.EventoProcessadoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventoProcessadoService {

    private final EventoProcessadoRepository repository;

    public EventoProcessadoService(EventoProcessadoRepository repository) {
        this.repository = repository;
    }

    public boolean jaProcessado(UUID eventoId) {
        return repository.existsByEventoId(eventoId);
    }

    public void marcarComoProcessado(UUID eventoId) {
        EventoProcessado evento = new EventoProcessado();
        evento.setEventoId(eventoId);
        repository.save(evento);
    }
}