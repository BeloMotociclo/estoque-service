package br.com.Belo.Motociclo.estoque_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "peca")
@Getter
@Setter
public class Peca extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private String categoria;

    private String marca;

    @Column(name = "preco_venda", nullable = false)
    private BigDecimal precoVenda;

    @Version
    private Integer version;

    @Column(nullable = false)
    private Boolean ativo = true;
}