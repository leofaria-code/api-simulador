package br.com.leo.apisimulador.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
@Service
public class TelemetriaService {

    private final Map<String, AtomicLong> volumeRequisicoes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> volumeErros = new ConcurrentHashMap<>();
    private final Map<String, List<Double>> temposResposta = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> tempoTotalRespostaNanos = new ConcurrentHashMap<>();

    /**
     * Executa uma operação com medição de tempo
     * 
     * @param nomeOperacao Nome da operação para registro
     * @param operacao     Operação a ser executada
     * @return Resultado da operação
     */
    public <T> T medirTempoExecucao(String nomeOperacao, Supplier<T> operacao) {
        long inicio = System.nanoTime();
        try {
            T resultado = operacao.get();
            long fim = System.nanoTime();
            registrarTempoResposta(nomeOperacao, Duration.ofNanos(fim - inicio));
            log.debug("Operação {} executada em {} ms", nomeOperacao,
                    Duration.ofNanos(fim - inicio).toMillis());
            return resultado;
        } catch (Exception e) {
            long fim = System.nanoTime();
            registrarTempoResposta(nomeOperacao, Duration.ofNanos(fim - inicio));
            log.error("Erro na operação {} após {} ms: {}", nomeOperacao,
                    Duration.ofNanos(fim - inicio).toMillis(), e.getMessage());
            throw e;
        }
    }

    /**
     * Registra o tempo de resposta de uma operação
     */
    public void registrarTempoResposta(String nomeOperacao, Duration duracao) {
        volumeRequisicoes
                .computeIfAbsent(nomeOperacao, k -> new AtomicLong(0))
                .incrementAndGet();

        tempoTotalRespostaNanos
                .computeIfAbsent(nomeOperacao, k -> new AtomicLong(0))
                .addAndGet(duracao.toNanos());
    }

    /**
     * Obtém dados consolidados de telemetria
     */
    public Map<String, Object> obterDadosTelemetria() {
        Map<String, Object> metricas = new TreeMap<>(); // Usando TreeMap para ordenação

        // Agrupando por endpoint
        Map<String, MetricasEndpoint> metricasPorEndpoint = new HashMap<>();

        for (String operacao : volumeRequisicoes.keySet()) {
            String endpoint = operacao.split(" ", 2)[1]; // Separa o método HTTP do path
            long volume = volumeRequisicoes.get(operacao).get();
            long tempoTotal = tempoTotalRespostaNanos.get(operacao).get();
            double tempoMedioMs = (double) tempoTotal / volume / 1_000_000.0;
            double tempoTotalMs = tempoTotal / 1_000_000.0;

            MetricasEndpoint endpointMetricas = metricasPorEndpoint.computeIfAbsent(endpoint,
                    k -> new MetricasEndpoint());

            endpointMetricas.volume = volume;
            endpointMetricas.tempoMedioMs = tempoMedioMs;
            endpointMetricas.tempoTotalMs = tempoTotalMs;
            endpointMetricas.operacao = operacao;
        }

        // Ordenando e formatando o resultado final
        metricasPorEndpoint.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    MetricasEndpoint m = entry.getValue();
                    String baseKey = entry.getKey();

                    Map<String, Object> endpointMetricas = new LinkedHashMap<>();
                    endpointMetricas.put("volume_requisicoes", m.volume);
                    endpointMetricas.put("tempo_medio_ms", String.format("%.2f", m.tempoMedioMs));
                    endpointMetricas.put("tempo_total_ms", String.format("%.2f", m.tempoTotalMs));

                    metricas.put(baseKey, endpointMetricas);
                });

        return metricas;
    }

    /**
     * Limpa os dados de telemetria
     */
    public void limparDados() {
        volumeRequisicoes.clear();
        tempoTotalRespostaNanos.clear();
    }

    private static class MetricasEndpoint {
        public String operacao;
        long volume;
        double tempoMedioMs;
        double tempoTotalMs;
    }

    public <T> T medirOperacaoSimulacao(String operacao, Supplier<T> execucao) {
        long inicio = System.currentTimeMillis();
        try {
            T resultado = execucao.get();
            registrarSucesso(operacao, System.currentTimeMillis() - inicio);
            return resultado;
        } catch (Exception e) {
            registrarErro(operacao, System.currentTimeMillis() - inicio);
            throw e;
        }
    }

    private void registrarSucesso(String operacao, long tempoExecucao) {
        volumeRequisicoes.computeIfAbsent(operacao, k -> new AtomicLong()).incrementAndGet();
        temposResposta.computeIfAbsent(operacao, k -> Collections.synchronizedList(new ArrayList<>()))
                .add((double) tempoExecucao);

        log.info("Operação '{}' completada em {}ms", operacao, tempoExecucao);
    }

    private void registrarErro(String operacao, long tempoExecucao) {
        volumeErros.computeIfAbsent(operacao, k -> new AtomicLong()).incrementAndGet();
        log.error("Erro na operação '{}' após {}ms", operacao, tempoExecucao);
    }

    public Map<String, Object> obterEstatisticas() {
        Map<String, Object> estatisticas = new HashMap<>();

        volumeRequisicoes.forEach((operacao, volume) -> {
            Map<String, Object> metricas = new HashMap<>();
            metricas.put("total_requisicoes", volume.get());
            metricas.put("total_erros", volumeErros.getOrDefault(operacao, new AtomicLong()).get());

            List<Double> tempos = temposResposta.getOrDefault(operacao, new ArrayList<>());
            if (!tempos.isEmpty()) {
                double tempoMedio = tempos.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                metricas.put("tempo_medio_ms", String.format("%.2f", tempoMedio));
                metricas.put("volume_ultimos_10min", tempos.size());
            }

            estatisticas.put(operacao, metricas);
        });

        return estatisticas;
    }
}
