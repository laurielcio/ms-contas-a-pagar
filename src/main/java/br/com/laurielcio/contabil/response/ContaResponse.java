package br.com.laurielcio.contabil.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.laurielcio.contabil.entity.SituacaoEnum;
import org.springframework.hateoas.RepresentationModel;

import br.com.laurielcio.contabil.entity.ContaEntity;
import lombok.Getter;

@Getter
public class ContaResponse extends RepresentationModel<ContaResponse>{

	private Long id;

	private LocalDate dataVencimento;

	private LocalDate dataPagamento;

	private BigDecimal valor;

	private String descricao;

	private SituacaoEnum situacao;
	
	public ContaResponse(ContaEntity entity) {
        this.id = entity.getId();
        this.dataVencimento = entity.getDataVencimento();
        this.dataPagamento = entity.getDataPagamento();
        this.valor = entity.getValor();
        this.descricao = entity.getDescricao();
        this.situacao = entity.getSituacao();
    }

}

