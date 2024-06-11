package br.com.laurielcio.contabil.service;

import br.com.laurielcio.contabil.entity.SituacaoEnum;
import br.com.laurielcio.contabil.request.ContaAtualizaRequest;
import br.com.laurielcio.contabil.request.ContaRequest;
import br.com.laurielcio.contabil.response.ContaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ContaService {

	ContaResponse cadastrarConta(ContaRequest request);

	ContaResponse obterContaPorId(Long id);

	Page<ContaResponse> obterListaContas(LocalDate dataVencimento, String descricao, Pageable pageable);

	List<ContaResponse> importarContasViaCSV(MultipartFile file);

	BigDecimal obterValorTotalPagoPorPeriodo(LocalDate dataInicial, LocalDate dataFinal);

	ContaResponse alterarSituacaoConta(Long id, SituacaoEnum situacao);

	ContaResponse atualizarConta(Long id, ContaAtualizaRequest request);
}

