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
public class ServicoTelemetria {

    private final Map<String, AtomicLong> volumeRequisicoes = new ConcurrentHashMap<>();
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
            return operacao.get();
        } finally {
            long fim = System.nanoTime();
            registrarTempoResposta(nomeOperacao, Duration.ofNanos(fim - inicio));
            log.debug("Operação {} executada em {} ms", nomeOperacao,
                    Duration.ofNanos(fim - inicio).toMillis());
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
}
