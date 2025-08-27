package br.com.leo.apisimulador.service;

import br.com.leo.apisimulador.dto.simulacao.ParcelaDTO;
import br.com.leo.apisimulador.dto.simulacao.ResultadoSimulacaoDTO;
import br.com.leo.apisimulador.dto.simulacao.SimulacaoRequestDTO;
import br.com.leo.apisimulador.dto.simulacao.SimulacaoResponseDTO;
import br.com.leo.apisimulador.dto.telemetria.VolumeSimuladoProdutoDTO;
import br.com.leo.apisimulador.dto.telemetria.VolumeSimuladoResponseDTO;
import br.com.leo.apisimulador.enums.TipoSimulacaoEnum;
import br.com.leo.apisimulador.model.h2.Simulacao;
import br.com.leo.apisimulador.model.sqlserver.Produto;
import br.com.leo.apisimulador.repository.h2.SimulacaoRepository;
import br.com.leo.apisimulador.repository.sqlserver.ProdutoRepository;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulacaoService {

    private final SimulacaoRepository simulacaoRepository;
    private final CalculoAmortizacaoService calculoService;
    private final EventHubProducerClient eventHubProducerClient;
    private final ObjectMapper objectMapper;
    private final TelemetriaService telemetria;
    private final ProdutoCacheService produtoCacheService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional("localTransactionManager")
    public SimulacaoResponseDTO simular(SimulacaoRequestDTO requisicao) {
        return telemetria.medirTempoExecucao("simulacao_emprestimo", () -> {
            try {
                Produto produto = buscarProdutoElegivel(requisicao);
                List<ResultadoSimulacaoDTO> resultados = calcularSimulacoes(requisicao, produto);

                // Criar e persistir a simulação
                Simulacao simulacao = criarSimulacao(requisicao, produto);

                // Criar a resposta com o record
                SimulacaoResponseDTO resposta = new SimulacaoResponseDTO(
                        null, // ID será atualizado após persistência
                        produto.getCodigoProduto(),
                        produto.getDescricaoProduto(),
                        produto.getTaxaJuros(),
                        resultados);

                // Persistir a simulação
                try {
                    simulacao.setResultadoJson(objectMapper.writeValueAsString(resposta));
                    simulacao = simulacaoRepository.save(simulacao);

                    // Criar nova resposta com ID atualizado
                    resposta = new SimulacaoResponseDTO(
                            simulacao.getIdSimulacao(),
                            produto.getCodigoProduto(),
                            produto.getDescricaoProduto(),
                            produto.getTaxaJuros(),
                            resultados);

                    // Atualizar o JSON com o ID correto
                    simulacao.setResultadoJson(objectMapper.writeValueAsString(resposta));
                    simulacaoRepository.save(simulacao);

                } catch (Exception e) {
                    log.error("Erro ao persistir simulação: {}", e.getMessage(), e);
                    throw new RuntimeException("Falha ao persistir simulação", e);
                }

                enviarParaEventHub(simulacao.getResultadoJson());

                log.info("Simulação concluída com sucesso");
                return resposta;

            } catch (Exception e) {
                log.error("Erro ao processar simulação: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao processar simulação: " + e.getMessage(), e);
            }
        });
    }

    private Produto buscarProdutoElegivel(SimulacaoRequestDTO requisicao) {
        return produtoCacheService.buscarProdutos().stream()
                .filter(p -> isProdutoElegivel(p, requisicao))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Nenhum produto encontrado para os parâmetros: valor={}, prazo={}",
                            requisicao.valorDesejado(), requisicao.prazo());
                    return new RuntimeException("Nenhum produto encontrado para os parâmetros informados.");
                });
    }

    private boolean isProdutoElegivel(Produto produto, SimulacaoRequestDTO requisicao) {
        // Verificar se os campos essenciais básicos não são nulos
        if (produto.getValorMinimo() == null || produto.getMinimoMeses() == null) {
            log.warn("Produto {} tem campos mínimos nulos - ignorando na elegibilidade", produto.getCodigoProduto());
            return false;
        }

        // Verificar valor mínimo
        if (requisicao.valorDesejado().compareTo(produto.getValorMinimo()) < 0) {
            return false;
        }

        // Verificar prazo mínimo
        if (requisicao.prazo() < produto.getMinimoMeses()) {
            return false;
        }

        // Verificar valor máximo (se não for nulo)
        if (produto.getValorMaximo() != null &&
                requisicao.valorDesejado().compareTo(produto.getValorMaximo()) > 0) {
            return false;
        }

        // Verificar prazo máximo (se não for nulo)
        if (produto.getMaximoMeses() != null &&
                requisicao.prazo() > produto.getMaximoMeses()) {
            return false;
        }

        return true;
    }

    private List<ResultadoSimulacaoDTO> calcularSimulacoes(SimulacaoRequestDTO requisicao, Produto produto) {
        List<ParcelaDTO> parcelasSAC = calculoService.calcularSAC(
                requisicao.valorDesejado(),
                produto.getTaxaJuros(),
                requisicao.prazo());

        List<ParcelaDTO> parcelasPRICE = calculoService.calcularPRICE(
                requisicao.valorDesejado(),
                produto.getTaxaJuros(),
                requisicao.prazo());

        return List.of(
                new ResultadoSimulacaoDTO(TipoSimulacaoEnum.SAC, parcelasSAC),
                new ResultadoSimulacaoDTO(TipoSimulacaoEnum.PRICE, parcelasPRICE));
    }

    private Simulacao criarSimulacao(SimulacaoRequestDTO requisicao, Produto produto) {
        Simulacao simulacao = new Simulacao();
        // simulacao.setProduto(produto);
        simulacao.setProdutoId(produto.getCodigoProduto());
        simulacao.setValorDesejado(requisicao.valorDesejado());
        simulacao.setPrazo(requisicao.prazo());
        return simulacao;
    }

    private void enviarParaEventHub(String mensagem) {
        try {
            log.info("🚀 Enviando simulação para Azure Event Hub...");
            log.debug("📄 Payload: {}", mensagem.substring(0, Math.min(100, mensagem.length())) + "...");

            eventHubProducerClient.send(List.of(new EventData(mensagem)));

            log.info("✅ Simulação enviada com sucesso para Azure Event Hub");
        } catch (Exception e) {
            log.error("❌ Erro ao enviar mensagem para EventHub: {}", e.getMessage(), e);
            log.error("🔍 Verifique: 1) Conexão de rede, 2) Credenciais do .env, 3) Permissões do Event Hub");
        }
    }

    public VolumeSimuladoResponseDTO calcularVolumeSimuladoPorDia(LocalDate data) {
        List<Simulacao> simulacoesDoDia = simulacaoRepository.findByDataReferencia(data);

        Map<Integer, List<Simulacao>> simulacoesPorProduto = simulacoesDoDia.stream()
                .collect(Collectors.groupingBy(Simulacao::getProdutoId));

        List<VolumeSimuladoProdutoDTO> produtosCalculados = simulacoesPorProduto.entrySet().stream()
                .map(entry -> {
                    Integer produtoId = entry.getKey();
                    List<Simulacao> simulacoes = entry.getValue();
                    return calcularMetricasProduto(produtoId, simulacoes);
                })
                .collect(Collectors.toList());

        return new VolumeSimuladoResponseDTO(data, produtosCalculados);
    }

    private VolumeSimuladoProdutoDTO calcularMetricasProduto(Integer produtoId, List<Simulacao> simulacoes) {
        if (simulacoes == null || simulacoes.isEmpty()) {
            return null;
        }

        String descricaoProduto = extrairDescricaoProduto(simulacoes.get(0).getResultadoJson());
        BigDecimal valorTotalDesejado = BigDecimal.ZERO;
        BigDecimal valorTotalCredito = BigDecimal.ZERO;
        BigDecimal somaTaxasJuros = BigDecimal.ZERO;
        BigDecimal somaValoresPrimeiraPrestacao = BigDecimal.ZERO;

        for (Simulacao s : simulacoes) {
            valorTotalDesejado = valorTotalDesejado.add(s.getValorDesejado());
            try {
                SimulacaoResponseDTO simResponse = objectMapper.readValue(s.getResultadoJson(),
                        SimulacaoResponseDTO.class);
                somaTaxasJuros = somaTaxasJuros.add(simResponse.taxaJuros());

                ResultadoSimulacaoDTO priceResult = simResponse.resultadosSimulacao().stream()
                        .filter(r -> r.tipo() == TipoSimulacaoEnum.PRICE).findFirst().orElse(null);

                if (priceResult != null) {
                    BigDecimal totalCreditoSimulacao = priceResult.parcelas().stream()
                            .map(ParcelaDTO::valorPrestacao)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    valorTotalCredito = valorTotalCredito.add(totalCreditoSimulacao);

                    if (!priceResult.parcelas().isEmpty()) {
                        somaValoresPrimeiraPrestacao = somaValoresPrimeiraPrestacao
                                .add(priceResult.parcelas().get(0).valorPrestacao());
                    }
                }

            } catch (Exception e) {
                log.error("Erro ao processar JSON da simulação {}: {}", s.getIdSimulacao(), e.getMessage());
            }
        }

        int quantidadeSimulacoes = simulacoes.size();
        BigDecimal taxaMedia = (quantidadeSimulacoes > 0)
                ? somaTaxasJuros.divide(BigDecimal.valueOf(quantidadeSimulacoes), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal valorMedioPrestacao = (quantidadeSimulacoes > 0)
                ? somaValoresPrimeiraPrestacao.divide(BigDecimal.valueOf(quantidadeSimulacoes), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new VolumeSimuladoProdutoDTO(
                produtoId,
                descricaoProduto,
                quantidadeSimulacoes,
                taxaMedia,
                valorTotalDesejado,
                valorTotalCredito,
                valorMedioPrestacao);
    }

    private String extrairDescricaoProduto(String resultadoJson) {
        try {
            SimulacaoResponseDTO simResponse = objectMapper.readValue(resultadoJson, SimulacaoResponseDTO.class);
            return simResponse.descricaoProduto();
        } catch (Exception e) {
            log.error("Erro ao extrair descrição do produto do JSON: {}", e.getMessage());
            return "Descrição Indisponível";
        }
    }

    /**
     * Força atualização do cache de produtos (método administrativo)
     */
    public void atualizarCacheProdutos() {
        log.info("🔄 Forçando atualização do cache de produtos...");
        produtoCacheService.forcarAtualizacao();
    }

    /**
     * Busca produto específico by ID usando cache
     */
    public Produto buscarProdutoPorId(Integer codigoProduto) {
        return produtoCacheService.buscarProdutos().stream()
                .filter(p -> p.getCodigoProduto().equals(codigoProduto))
                .findFirst()
                .orElse(null);
    }
}