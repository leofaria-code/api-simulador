package br.com.leo.apisimulador.controller;

import br.com.leo.apisimulador.dto.ErrorResponse;
import br.com.leo.apisimulador.dto.SimulacaoGetResponse;
import br.com.leo.apisimulador.dto.SimulacaoRequest;
import br.com.leo.apisimulador.dto.SimulacaoResponse;
import br.com.leo.apisimulador.model.h2.Simulacao;
import br.com.leo.apisimulador.repository.h2.SimulacaoRepository;
import br.com.leo.apisimulador.service.ServicoTelemetria;
import br.com.leo.apisimulador.service.SimulacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gerenciar simulações de crédito.
 */
@Tag(name = "Simulações", description = "Endpoints para gerenciamento de simulações de empréstimo")
@RestController
@RequestMapping("/simulacoes")
@Slf4j
public class SimulacaoController {

    private static final String ENDPOINT_SIMULAR = "POST /simulacoes";
    private static final String ENDPOINT_LISTAR = "GET /simulacoes";
    private static final String ENDPOINT_AGREGADO_DIA = "GET /simulacoes/agregado-por-dia-e-produto";
    private static final String ENDPOINT_AGREGADO_PRODUTO = "GET /simulacoes/agregado-por-produto-e-data";
    private static final String ENDPOINT_TELEMETRIA = "GET /simulacoes/telemetria";

    private final ObjectMapper objectMapper;
    private final SimulacaoService servicoSimulacao;
    private final SimulacaoRepository repositorioSimulacao;
    private final ServicoTelemetria servicoTelemetria;

    @Autowired
    public SimulacaoController(SimulacaoService servicoSimulacao,
            SimulacaoRepository repositorioSimulacao,
            ServicoTelemetria servicoTelemetria,
            ObjectMapper objectMapper) {
        this.servicoSimulacao = servicoSimulacao;
        this.repositorioSimulacao = repositorioSimulacao;
        this.servicoTelemetria = servicoTelemetria;
        this.objectMapper = objectMapper;
    }

    /**
     * Realiza uma nova simulação de crédito.
     * 
     * @param requisicao Os parâmetros da simulação
     * @return Resposta contendo os resultados da simulação
     */
    @Operation(summary = "Realiza uma simulação de empréstimo", description = "Calcula as parcelas usando os sistemas SAC e PRICE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Simulação realizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimulacaoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SimulacaoResponse> simular(@Valid @RequestBody SimulacaoRequest requisicao) {
        log.info("Iniciando simulação para valor: {} e prazo: {}",
                requisicao.valorDesejado(), requisicao.prazo());

        SimulacaoResponse resposta = servicoTelemetria.medirTempoExecucao(
                ENDPOINT_SIMULAR,
                () -> servicoSimulacao.simular(requisicao));

        return ResponseEntity.ok(resposta);
    }

    /**
     * Recupera todas as simulações realizadas.
     * 
     * @return Lista de simulações armazenadas
     */
    @GetMapping
    public ResponseEntity<List<SimulacaoGetResponse>> obterTodasSimulacoes() {
        List<SimulacaoGetResponse> simulacoes = servicoTelemetria.medirTempoExecucao(
                ENDPOINT_LISTAR,
                () -> repositorioSimulacao.findAll().stream()
                        .map(this::converterParaGetResponse)
                        .collect(Collectors.toList()));

        return ResponseEntity.ok(simulacoes);
    }

    /**
     * Recupera simulações agregadas por dia e produto.
     * 
     * @param data A data para filtrar as simulações
     * @return Lista de simulações agregadas
     */
    @GetMapping("/agregado-por-dia-e-produto/{data}")
    public ResponseEntity<List<SimulacaoGetResponse>> obterSimulacoesPorDiaEProduto(
            @PathVariable LocalDate data) {
        log.info("Buscando simulações para a data: {}", data);

        List<SimulacaoGetResponse> resultados = servicoTelemetria.medirTempoExecucao(
                ENDPOINT_AGREGADO_DIA,
                () -> repositorioSimulacao.findByDataReferencia(data).stream()
                        .map(this::converterParaGetResponse)
                        .collect(Collectors.toList()));

        return ResponseEntity.ok(resultados);
    }

    /**
     * Recupera simulações agregadas por produto e data.
     * 
     * @param produtoId O ID do produto para filtrar as simulações
     * @param data      A data para filtrar as simulações
     * @return Lista de simulações agregadas
     */
    @GetMapping("/agregado-por-produto-e-data/{produtoId}/{data}")
    public ResponseEntity<List<SimulacaoGetResponse>> obterSimulacoesPorProdutoEData(
            @PathVariable Integer produtoId,
            @PathVariable LocalDate data) {
        log.info("Buscando simulações para produto: {} e data: {}", produtoId, data);

        List<SimulacaoGetResponse> resultados = servicoTelemetria.medirTempoExecucao(
                ENDPOINT_AGREGADO_PRODUTO,
                () -> repositorioSimulacao.findByProdutoIdAndDataReferencia(produtoId, data).stream()
                        .map(this::converterParaGetResponse)
                        .collect(Collectors.toList()));

        return ResponseEntity.ok(resultados);
    }

    /**
     * Obtém dados de telemetria da API.
     * 
     * @return Métricas de uso e performance
     */
    @GetMapping("/telemetria")
    public ResponseEntity<Map<String, Object>> obterDadosTelemetria() {
        return ResponseEntity.ok(servicoTelemetria.medirTempoExecucao(
                ENDPOINT_TELEMETRIA,
                servicoTelemetria::obterDadosTelemetria));
    }

    private SimulacaoGetResponse converterParaGetResponse(Simulacao simulacao) {
        String descricaoProduto = extrairDescricaoProduto(simulacao.getResultadoJson());

        return new SimulacaoGetResponse(
                simulacao.getIdSimulacao(),
                simulacao.getProdutoId(),
                descricaoProduto,
                simulacao.getValorDesejado(),
                simulacao.getPrazo(),
                simulacao.getDataReferencia(),
                simulacao.getResultadoJson());
    }

    private String extrairDescricaoProduto(String resultadoJson) {
        try {
            SimulacaoResponse simResponse = objectMapper.readValue(
                    resultadoJson,
                    SimulacaoResponse.class);
            return simResponse.descricaoProduto();
        } catch (Exception e) {
            log.error("Erro ao extrair descrição do produto do JSON: {}", e.getMessage());
            return "Descrição não disponível";
        }
    }
}