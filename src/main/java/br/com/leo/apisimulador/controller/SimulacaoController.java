package br.com.leo.apisimulador.controller;

import br.com.leo.apisimulador.dto.*;
import br.com.leo.apisimulador.model.h2.Simulacao;
import br.com.leo.apisimulador.repository.h2.SimulacaoRepository;
import br.com.leo.apisimulador.service.SimulacaoService;
import br.com.leo.apisimulador.service.TelemetriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
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
    private static final int TAMANHO_PAGINA_PADRAO = 200;

    private final ObjectMapper objectMapper;
    private final SimulacaoService servicoSimulacao;
    private final SimulacaoRepository repositorioSimulacao;
    private final TelemetriaService servicoTelemetria;

    public SimulacaoController(SimulacaoService servicoSimulacao,
            SimulacaoRepository repositorioSimulacao,
            TelemetriaService servicoTelemetria,
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
            @ApiResponse(responseCode = "200", description = "Simulação realizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimulacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<SimulacaoResponseDTO> simular(@Valid @RequestBody SimulacaoRequestDTO requisicao) {
        log.info("Iniciando simulação para valor: {} e prazo: {}",
                requisicao.valorDesejado(), requisicao.prazo());

        try {
            SimulacaoResponseDTO resposta = servicoTelemetria.medirTempoExecucao(
                    ENDPOINT_SIMULAR,
                    () -> servicoSimulacao.simular(requisicao));

            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            log.error("Erro ao processar simulação: {}", e.getMessage(), e);
            throw e; // Relançar para ser tratado pelo handler global
        }
    }

    /**
     * Recupera todas as simulações realizadas com paginação.
     * 
     * @param pagina  O número da página (começando de 0)
     * @param tamanho Tamanho da página (default: 200)
     * @return Lista paginada de simulações armazenadas
     */
    @Operation(summary = "Lista todas as simulações realizadas", description = "Retorna uma lista paginada de simulações (200 por página por padrão)")
    @GetMapping
    public ResponseEntity<Map<String, Object>> obterTodasSimulacoes(
            @Parameter(description = "Número da página (começando de 0)") @RequestParam(defaultValue = "0") int pagina,

            @Parameter(description = "Tamanho da página (máximo 200)") @RequestParam(defaultValue = "200") int tamanho) {

        // Validar e limitar o tamanho da página
        if (tamanho > TAMANHO_PAGINA_PADRAO) {
            tamanho = TAMANHO_PAGINA_PADRAO;
        }

        Pageable paginacao = PageRequest.of(pagina, tamanho, Sort.by("idSimulacao").descending());

        Page<Simulacao> paginaSimulacoes = servicoTelemetria.medirTempoExecucao(
                ENDPOINT_LISTAR,
                () -> repositorioSimulacao.findAll(paginacao));

        List<SimulacaoResumoDTO> registrosResumidos = paginaSimulacoes.getContent().stream()
                .map(this::converterParaResumoSimulacao)
                .collect(Collectors.toList());

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("pagina", paginaSimulacoes.getNumber() + 1); // Ajuste para iniciar em 1
        resposta.put("qtdRegistros", paginaSimulacoes.getTotalElements());
        resposta.put("qtdRegistrosPagina", tamanho);
        resposta.put("registros", registrosResumidos);

        return ResponseEntity.ok(resposta);
    }

    /**
     * Recupera simulações agregadas por dia e produto.
     * 
     * @param data A data para filtrar as simulações
     * @return Lista de simulações agregadas
     */
    @Operation(summary = "Lista simulações por dia", description = "Retorna as simulações realizadas em uma data específica")
    @GetMapping("/agregado-por-dia/{data}")
    public ResponseEntity<List<SimulacaoGetResponseDTO>> obterSimulacoesPorDia(
            @PathVariable LocalDate data) {
        log.info("Buscando simulações para a data: {}", data);

        List<SimulacaoGetResponseDTO> resultados = servicoTelemetria.medirTempoExecucao(
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
    @Operation(summary = "Lista simulações por produto e data", description = "Retorna as simulações para um produto específico em uma data")
    @GetMapping("/agregado-por-produto-e-data/{produtoId}/{data}")
    public ResponseEntity<List<SimulacaoGetResponseDTO>> obterSimulacoesPorProdutoEData(
            @PathVariable Integer produtoId,
            @PathVariable LocalDate data) {
        log.info("Buscando simulações para produto: {} e data: {}", produtoId, data);

        List<SimulacaoGetResponseDTO> resultados = servicoTelemetria.medirTempoExecucao(
                ENDPOINT_AGREGADO_PRODUTO,
                () -> repositorioSimulacao.findByProdutoIdAndDataReferencia(produtoId, data).stream()
                        .map(this::converterParaGetResponse)
                        .collect(Collectors.toList()));

        return ResponseEntity.ok(resultados);
    }

    /**
     * Recupera todas as simulações agregadas por produto.
     * 
     * @return Lista de simulações agregadas por produto
     */
    @Operation(summary = "Lista simulações por produto", description = "Retorna todas as simulações agrupadas por produto")
    @GetMapping("/agregado-por-produto")
    public ResponseEntity<Map<Integer, List<SimulacaoGetResponseDTO>>> obterSimulacoesPorProduto() {
        log.info("Buscando todas as simulações agrupadas por produto");

        Map<Integer, List<SimulacaoGetResponseDTO>> resultados = servicoTelemetria.medirTempoExecucao(
                "GET /simulacoes/agregado-por-produto",
                () -> repositorioSimulacao.findAll().stream()
                        .map(this::converterParaGetResponse)
                        .collect(Collectors.groupingBy(SimulacaoGetResponseDTO::produtoId)));

        return ResponseEntity.ok(resultados);
    }

    private SimulacaoGetResponseDTO converterParaGetResponse(Simulacao simulacao) {
        String descricaoProduto = extrairDescricaoProduto(simulacao.getResultadoJson());

        return new SimulacaoGetResponseDTO(
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
            SimulacaoResponseDTO simResponse = objectMapper.readValue(
                    resultadoJson,
                    SimulacaoResponseDTO.class);
            return simResponse.descricaoProduto();
        } catch (Exception e) {
            log.error("Erro ao extrair descrição do produto do JSON: {}", e.getMessage());
            return "Descrição não disponível";
        }
    }

    /**
     * Converte uma entidade Simulacao para um DTO resumido com valores totais.
     */
    private SimulacaoResumoDTO converterParaResumoSimulacao(Simulacao simulacao) {
        // Extrair valores totais do JSON primeiro para calcular
        BigDecimal valorTotalSAC = BigDecimal.ZERO;
        BigDecimal valorTotalPrice = BigDecimal.ZERO;

        try {
            SimulacaoResponseDTO simResponse = objectMapper.readValue(
                    simulacao.getResultadoJson(),
                    SimulacaoResponseDTO.class);

            // Calcular totais SAC e PRICE
            valorTotalSAC = calcularValorTotalSistema(simResponse, "SAC");
            valorTotalPrice = calcularValorTotalSistema(simResponse, "PRICE");

        } catch (Exception e) {
            log.error("Erro ao calcular valores totais da simulação {}: {}",
                    simulacao.getIdSimulacao(), e.getMessage());
        }

        return new SimulacaoResumoDTO(
            simulacao.getIdSimulacao(),
            simulacao.getValorDesejado().setScale(2, java.math.RoundingMode.HALF_UP),
            simulacao.getPrazo(),
            valorTotalSAC.setScale(2, java.math.RoundingMode.HALF_UP),
            valorTotalPrice.setScale(2, java.math.RoundingMode.HALF_UP)
        );
    }

    /**
     * Calcula o valor total de prestações para um determinado sistema de
     * amortização.
     */
    private BigDecimal calcularValorTotalSistema(SimulacaoResponseDTO simResponse, String tipoSistema) {
        return simResponse.resultadosSimulacao().stream()
                .filter(resultado -> resultado.tipo().name().equals(tipoSistema))
                .findFirst()
                .map(resultado -> resultado.parcelas().stream()
                        .map(parcela -> parcela.valorPrestacao())
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO)
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}