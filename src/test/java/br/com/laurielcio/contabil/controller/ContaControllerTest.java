package br.com.laurielcio.contabil.controller;

import br.com.laurielcio.contabil.entity.ContaEntity;
import br.com.laurielcio.contabil.entity.SituacaoEnum;
import br.com.laurielcio.contabil.request.ContaAtualizaRequest;
import br.com.laurielcio.contabil.request.ContaRequest;
import br.com.laurielcio.contabil.response.ContaResponse;
import br.com.laurielcio.contabil.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class ContaControllerTest {

    @InjectMocks
    private ContaController contaController;
    @Mock
    private ContaService contaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ContaEntity criarConta = new ContaEntity();
        criarConta.setId(1L);
        criarConta.setDataVencimento(LocalDate.now());
        criarConta.setDataPagamento(LocalDate.now().plusDays(20));
        criarConta.setDescricao("Teste");
        criarConta.setSituacao(SituacaoEnum.PENDENTE);

        ContaResponse response = new ContaResponse(criarConta);

        when(contaService.cadastrarConta(any(ContaRequest.class))).thenReturn(response);
        when(contaService.atualizarConta(anyLong(), any(ContaAtualizaRequest.class))).thenReturn(response);
        when(contaService.alterarSituacaoConta(anyLong(), any(SituacaoEnum.class))).thenReturn(response);
        when(contaService.obterContaPorId(anyLong())).thenReturn(response);
        when(contaService.obterValorTotalPagoPorPeriodo(any(LocalDate.class), any(LocalDate.class))).thenReturn(new BigDecimal(250));
    }

    @Test
    public void testCadastrarConta() {
        ResponseEntity<ContaResponse> response = contaController.cadastrarConta(new ContaRequest());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testAtualizarConta() {
        ResponseEntity<ContaResponse> response = contaController.atualizarConta(1L, new ContaAtualizaRequest());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAlterarSituacaoConta() {
        ResponseEntity<ContaResponse> response = contaController.alterarSituacaoConta(1L, SituacaoEnum.PENDENTE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testObterListaContas() {
        LocalDate dataVencimento = LocalDate.of(2024, 6, 11);
        String descricao = "Teste";
        int page = 0;
        int size = 10;

        ContaEntity contaEntity = new ContaEntity();

        Page<ContaResponse> contasResponse = new PageImpl<>(Collections.singletonList(new ContaResponse(contaEntity)));
        when(contaService.obterListaContas(eq(dataVencimento), eq(descricao), any(Pageable.class))).thenReturn(contasResponse);

        ResponseEntity<Page<ContaResponse>> responseEntity = contaController.obterListaContas(dataVencimento, descricao, page, size);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(contasResponse, responseEntity.getBody());
    }

    @Test
    public void testObterContaPorId() {
        ResponseEntity<ContaResponse> response = contaController.obterContaPorId(anyLong());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testObterValorTotalPagoPorPeriodo() {
        LocalDate dtInicio = LocalDate.now();
        LocalDate dtFim = LocalDate.now().plusDays(20);

        ResponseEntity<Map<String, Object>> response = contaController.obterValorTotalPagoPorPeriodo(dtInicio, dtFim);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testImportarContasViaCSV() {
        MultipartFile file = null;

        ContaEntity conta = new ContaEntity();
        conta.setId(1L);
        conta.setDataVencimento(LocalDate.now());
        conta.setDataPagamento(LocalDate.now().plusDays(20));
        conta.setDescricao("Teste");
        conta.setSituacao(SituacaoEnum.PENDENTE);

        ContaResponse response = new ContaResponse(conta);

        List<ContaResponse> list = new ArrayList<>();

        list.add(response);

        try {
            when(contaService.importarContasViaCSV(eq(file))).thenReturn(list);

            ResponseEntity<?> responseEntity = contaController.importarContasViaCSV(file);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

            if (!list.isEmpty()) {
                assertTrue(responseEntity.getBody() instanceof List);
                List<ContaResponse> responseBody = (List<ContaResponse>) responseEntity.getBody();
                assertEquals(list.size(), responseBody.size());
            } else {
                assertEquals("Nenhuma conta foi importada.", responseEntity.getBody());
            }
        } catch (Exception e) {
            fail("Exceção lançada durante o teste: " + e.getMessage());
        }
    }

}
