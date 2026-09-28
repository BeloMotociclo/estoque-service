package br.com.Belo.Motociclo.estoque_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "item_nota_pendente")
@Getter
@Setter
public class ItemNotaPendente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_fiscal_id", nullable = false)
    private NotaFiscal notaFiscal;

    @Column(nullable = false)
    private String codigo;

    private String descricao;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private BigDecimal precoUnitario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusItemPendente status = StatusItemPendente.PENDENTE;

    @Enumerated(EnumType.STRING)
    private ResolucaoPendente resolucao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peca_id")
    private Peca peca;
}
