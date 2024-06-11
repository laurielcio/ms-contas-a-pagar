package br.com.laurielcio.contabil.controller;

import br.com.laurielcio.contabil.entity.SituacaoEnum;
import br.com.laurielcio.contabil.request.ContaAtualizaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.laurielcio.contabil.request.ContaRequest;
import br.com.laurielcio.contabil.response.ContaResponse;
import br.com.laurielcio.contabil.service.ContaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contas")
@Tag(name = "Contas a Pagar", description = "API para gerenciamento de contas a pagar")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping("/cadastrar")
    @Operation(summary = "Cadastra conta", description = "Cadastra uma nova conta a pagar.")
    public ResponseEntity<ContaResponse> cadastrarConta(@RequestBody ContaRequest request) {
        ContaResponse response = contaService.cadastrarConta(request);

        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContaController.class).obterContaPorId(response.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/atualizar/{id}")
    @Operation(summary = "Atualizar um conta", description = "Atualiza todos atributos de um conta a pagar.")
    public ResponseEntity<ContaResponse> atualizarConta(@PathVariable Long id, @RequestBody ContaAtualizaRequest request) {

        ContaResponse response = contaService.atualizarConta(id, request);

        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContaController.class).obterContaPorId(response.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/alterar-situacao/{id}")
    @Operation(summary = "Alterar a situação de uma conta", description = "Altera a situação de uma conta entre PAGA e PENDENTE.")
    public ResponseEntity<ContaResponse> alterarSituacaoConta(@PathVariable Long id, @RequestParam SituacaoEnum situacao) { // Implementação do método return

        ContaResponse response = contaService.alterarSituacaoConta(id, situacao);

        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContaController.class).obterContaPorId(response.getId())).withSelfRel());


        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/lista")
    @Operation(summary = "Obter lista de contas", description = "Obtém lista de contas a pagar. Obs.: dataVencimento e descricao são opcionais, neste caso, o retorno será todos os registros existentes no banco. Se dataVencimento ou descricao for informada, será filtrado pelo parâmetro informado ou pelos dois, caso ambos forem informados. O filtro por descricao ignora maiúsculas e minúsculas na comparação e verifica se a descrição contém a sequência de caracteres fornecida (busca parcial).  ")
    public ResponseEntity<Page<ContaResponse>> obterListaContas(
            @Parameter(description = "Data de vencimento")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimento,

            @Parameter(description = "Descrição da conta")
            @RequestParam(required = false) String descricao,

            @Parameter(description = "Número da página")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ContaResponse> response = contaService.obterListaContas(dataVencimento, descricao, pageable);

        response.getContent().forEach(this::addLinkToConta);

        return ResponseEntity.ok(response);
    }

    private void addLinkToConta(ContaResponse contaResponse) {
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContaController.class)
                        .obterContaPorId(contaResponse.getId()))
                .withSelfRel();
        contaResponse.add(selfLink);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter conta por ID", description = "Obtém conta a pagar por ID.")
    public ResponseEntity<ContaResponse> obterContaPorId(@PathVariable Long id) {
        ContaResponse response = contaService.obterContaPorId(id);
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContaController.class).obterContaPorId(response.getId())).withSelfRel());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/valor-total-pago")
    @Operation(summary = "Obter valor total pago por período", description = "Obtém valor total das contas pagas por período.")
    public ResponseEntity<Map<String, Object>> obterValorTotalPagoPorPeriodo(
            @Parameter(description = "Data inicial")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Data final")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        BigDecimal valorTotal = contaService.obterValorTotalPagoPorPeriodo(dataInicial, dataFinal);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200L);
        response.put("valorTotal", "R$ " + valorTotal);
        response.put("description", "Entre " + dataFinal + " e " + dataFinal);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/importar-csv")
    @Operation(summary = "Importar contas via arquivo CSV", description = "Importa contas a pagar através de arquivo CSV.")
    public ResponseEntity<?> importarContasViaCSV(
            @Parameter(description = "Arquivo CSV contendo as contas a pagar") @RequestParam("file") MultipartFile file) {

        try {
            List<ContaResponse> response = contaService.importarContasViaCSV(file);

            if (!response.isEmpty()) {
                response.forEach(this::addLinkToConta);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok("Nenhuma conta foi importada.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao importar contas: " + e.getMessage());
        }
    }


}

