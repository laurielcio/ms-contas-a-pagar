package br.com.laurielcio.contabil.request;

import br.com.laurielcio.contabil.entity.SituacaoEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContaAtualizaRequest {

    @NotNull(message = "A data de vencimento não pode ser nula")
    private LocalDate dataVencimento;

    @NotNull(message = "O valor não pode ser nulo")
    private BigDecimal valor;

    @NotEmpty(message = "A descrição não pode ser vazia")
    private String descricao;

    @NotNull(message = "A situação não pode ser nula")
    private SituacaoEnum situacao;
}
