package br.com.laurielcio.contabil.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.laurielcio.contabil.entity.SituacaoEnum;
import br.com.laurielcio.contabil.exception.ImportacaoContaException;
import br.com.laurielcio.contabil.request.ContaAtualizaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.laurielcio.contabil.request.ContaRequest;
import br.com.laurielcio.contabil.response.ContaResponse;
import br.com.laurielcio.contabil.entity.ContaEntity;
import br.com.laurielcio.contabil.exception.ContaNotFoundException;
import br.com.laurielcio.contabil.repository.ContaRepository;
import br.com.laurielcio.contabil.service.ContaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

    @Autowired
	private ContaRepository contaRepository;

	@Override
	public ContaResponse cadastrarConta(ContaRequest request) {
		log.info("Iniciando o cadastro de conta a pagar...");
		
		ContaEntity conta = new ContaEntity(request);
		
		contaRepository.save(conta);
		
		return new ContaResponse(conta);
	}

	@Override
    public ContaResponse obterContaPorId(Long id) {
        log.info("Obtendo conta por id...");

        Optional<ContaEntity> optionalConta = contaRepository.findById(id);

        if (optionalConta.isPresent()) {
            ContaEntity conta = optionalConta.get();
            return new ContaResponse(conta);
        } else {
            throw new ContaNotFoundException("Nenhuma conta a pagar localizada com o id: " + id);
        }
    }

    @Override
    public Page<ContaResponse> obterListaContas(LocalDate dataVencimento, String descricao, Pageable pageable) {
        log.info("Obtendo lista de contas a pagar por data de vencimento e descrição...");

        Page<ContaEntity> contas;

        if (dataVencimento != null && descricao != null) {
            contas = contaRepository.findByDataVencimentoAndDescricaoContainingIgnoreCase(dataVencimento, descricao, pageable);
        } else if (dataVencimento != null) {
            contas = contaRepository.findByDataVencimento(dataVencimento, pageable);
        } else if (descricao != null) {
            contas = contaRepository.findByDescricaoContainingIgnoreCase(descricao, pageable);
        } else {
            contas = contaRepository.findAll(pageable);
        }

        if (contas.isEmpty()) {
            throw new ContaNotFoundException("Nenhuma conta a pagar encontrada com os parâmetros fornecidos.");
        }

        Page<ContaResponse> response = contas.map(ContaResponse::new);

        return response;
    }

    @Override
    public List<ContaResponse> importarContasViaCSV(MultipartFile file) {
        log.info("Importando contas a pagar via arquivo csv...");

        List<ContaResponse> response = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                ContaEntity conta = criarContaEntity(data);
                contaRepository.save(conta);

                response.add(new ContaResponse(conta));
            }
        } catch (IOException e) {
            log.error("Erro ao importar contas via arquivo CSV: {}", e.getMessage());
            throw new ImportacaoContaException("Erro ao importar contas via arquivo CSV", e);
        }
        return response;
    }

    @Override
    public BigDecimal obterValorTotalPagoPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        log.info("Obtendo o valot total pago por período...");

        BigDecimal response = contaRepository.findValorTotalPorPeriodo(dataInicial, dataFinal);

        return response;
    }

    @Override
    public ContaResponse alterarSituacaoConta(Long id, SituacaoEnum situacao) {
        log.info("Alterando a situação de um conta...");

        Optional<ContaEntity> optionalConta = contaRepository.findById(id);

        if (optionalConta.isPresent()) {
            ContaEntity conta = optionalConta.get();
            conta.setSituacao(situacao);

            if (situacao.equals(SituacaoEnum.PAGA)) {

                conta.setDataPagamento(LocalDate.now());
            } else {
                conta.setDataPagamento(null);
            }

            contaRepository.save(conta);

            return new ContaResponse(conta);
        } else {
            throw new ContaNotFoundException("Nenhuma conta a pagar localizada com o id: " + id);
        }
    }

    @Override
    public ContaResponse atualizarConta(Long id, ContaAtualizaRequest request) {
        log.info("Atualizando um conta...");

        Optional<ContaEntity> optionalConta = contaRepository.findById(id);

        if (optionalConta.isPresent()) {
            ContaEntity conta = optionalConta.get();
            conta.setDataVencimento(request.getDataVencimento());
            conta.setValor(request.getValor());
            conta.setSituacao(request.getSituacao());

            if (request.getSituacao().equals(SituacaoEnum.PAGA)) {
                conta.setDataPagamento(LocalDate.now());
            } else {
                conta.setDataPagamento(null);
            }

            contaRepository.save(conta);

            return new ContaResponse(conta);
        } else {
            throw new ContaNotFoundException("Nenhuma conta a pagar localizada com o id: " + id);
        }
    }


    private ContaEntity criarContaEntity(String[] data) {
        if (data.length != 3) {
            throw new IllegalArgumentException("Número incorreto de colunas: " + data.length);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        ContaRequest request = new ContaRequest();
        request.setDataVencimento(LocalDate.parse(data[0], dateFormatter));
        request.setValor(new BigDecimal(data[1]));
        request.setDescricao(data[2]);

        return new ContaEntity(request);
    }




}

