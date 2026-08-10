package br.com.Belo.Motociclo.estoque_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "nota_fiscal")
@Getter
@Setter
public class NotaFiscal extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @Column(nullable = false)
    private String numero;

    @Column(name = "chave_acesso", unique = true)
    private String chaveAcesso;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private Boolean ativo = true;

    // getters e setters
}

