package br.com.Belo.Motociclo.estoque_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "log_alteracao")
@Getter
@Setter
public class LogAlteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID usuarioId;

    @Column(nullable = false)
    private String entidade;

    @Column(nullable = false)
    private String entidadeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcaoLog acao;

    @Column(columnDefinition = "TEXT")
    private String detalhes;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();
}