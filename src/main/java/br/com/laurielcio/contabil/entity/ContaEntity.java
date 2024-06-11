package br.com.laurielcio.contabil.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import br.com.laurielcio.contabil.request.ContaRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "contas_a_pagar")
public class ContaEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento", nullable = true)
    private LocalDate dataPagamento;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false)
    private SituacaoEnum situacao;
    
    public ContaEntity(ContaRequest request) {
		this.dataVencimento = request.getDataVencimento();
		this.valor = request.getValor();
		this.descricao = request.getDescricao();
		this.situacao = SituacaoEnum.PENDENTE;
	}
}

