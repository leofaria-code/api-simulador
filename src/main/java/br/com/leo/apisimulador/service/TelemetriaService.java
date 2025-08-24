package br.com.leo.apisimulador.service;

import br.com.leo.apisimulador.dto.TelemetriaEndpointDTO;
import br.com.leo.apisimulador.dto.TelemetriaResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
@Service
public class TelemetriaService {

    private final Map<String, AtomicLong> volumeRequisicoes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> tempoTotalRespostaNanos = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> volumeSucesso = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> temposRespostaMs = new ConcurrentHashMap<>();

    /**
     * Executa uma operação com medição de tempo
     * 
     * @param nomeOperacao Nome da operação para registro
     * @param operacao     Operação a ser executada
     * @return Resultado da operação
     */
    public <T> T medirTempoExecucao(String nomeOperacao, Supplier<T> operacao) {
        long inicio = System.nanoTime();
        boolean sucesso = false;
        try {
            T resultado = operacao.get();
            sucesso = true;
            long fim = System.nanoTime();
            registrarTempoResposta(nomeOperacao, Duration.ofNanos(fim - inicio), sucesso);
            log.debug("Operação {} executada em {} ms", nomeOperacao,
                    Duration.ofNanos(fim - inicio).toMillis());
            return resultado;
        } catch (Exception e) {
            long fim = System.nanoTime();
            registrarTempoResposta(nomeOperacao, Duration.ofNanos(fim - inicio), sucesso);
            log.error("Erro na operação {} após {} ms: {}", nomeOperacao,
                    Duration.ofNanos(fim - inicio).toMillis(), e.getMessage());
            throw e;
        }
    }

    /**
     * Registra o tempo de resposta de uma operação
     */
    public void registrarTempoResposta(String nomeOperacao, Duration duracao, boolean sucesso) {
        volumeRequisicoes
                .computeIfAbsent(nomeOperacao, k -> new AtomicLong(0))
                .incrementAndGet();

        if (sucesso) {
            volumeSucesso
                    .computeIfAbsent(nomeOperacao, k -> new AtomicLong(0))
                    .incrementAndGet();
        }
        // Note: removed error tracking since it's no longer needed

        tempoTotalRespostaNanos
                .computeIfAbsent(nomeOperacao, k -> new AtomicLong(0))
                .addAndGet(duracao.toNanos());

        // Armazenar tempos individuais para cálculo de min/max
        temposRespostaMs
                .computeIfAbsent(nomeOperacao, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(duracao.toMillis());
    }

    /**
     * Obtém dados de telemetria no formato especificado
     */
    public TelemetriaResponseDTO obterTelemetriaFormatada(LocalDate dataReferencia) {
        List<TelemetriaEndpointDTO> endpoints = new ArrayList<>();

        // Mapear os endpoints conhecidos da SimulacaoController
        Map<String, String> endpointNames = Map.of(
                "POST /simulacoes", "Simulacao - Criar",
                "GET /simulacoes", "Simulacao - Listar",
                "GET /simulacoes/dia", "Simulacao - Volume por Dia"
        );

        for (String operacao : volumeRequisicoes.keySet()) {
            if (endpointNames.containsKey(operacao)) {
                long qtdRequisicoes = volumeRequisicoes.get(operacao).get();
                long qtdSucesso = volumeSucesso.getOrDefault(operacao, new AtomicLong(0)).get();
                List<Long> tempos = temposRespostaMs.getOrDefault(operacao, Collections.emptyList());

                if (qtdRequisicoes > 0 && !tempos.isEmpty()) {
                    // Calcular métricas de tempo
                    long tempoMedio = (long) tempos.stream()
                            .mapToLong(Long::longValue)
                            .average()
                            .orElse(0.0);
                    
                    long tempoMinimo = tempos.stream()
                            .mapToLong(Long::longValue)
                            .min()
                            .orElse(0L);
                    
                    long tempoMaximo = tempos.stream()
                            .mapToLong(Long::longValue)
                            .max()
                            .orElse(0L);

                    // Calcular percentual de sucesso
                    double percentualSucesso = (double) qtdSucesso / qtdRequisicoes;

                    TelemetriaEndpointDTO endpointDTO = new TelemetriaEndpointDTO(
                            endpointNames.get(operacao),
                            qtdRequisicoes,
                            tempoMedio,
                            tempoMinimo,
                            tempoMaximo,
                            percentualSucesso
                    );

                    endpoints.add(endpointDTO);
                }
            }
        }

        return new TelemetriaResponseDTO(dataReferencia, endpoints);
    }

    /**
     * Obtém dados de telemetria no formato Map para compatibilidade
     */
    public Map<String, Object> obterDadosTelemetria() {
        Map<String, Object> dados = new HashMap<>();
        
        // Calcular métricas gerais
        long totalRequisicoes = volumeRequisicoes.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        
        long totalSucesso = volumeSucesso.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        
        double percentualSucessoGeral = totalRequisicoes > 0 ? 
                (double) totalSucesso / totalRequisicoes : 0.0;
        
        dados.put("totalRequisicoes", totalRequisicoes);
        dados.put("totalSucesso", totalSucesso);
        dados.put("percentualSucessoGeral", percentualSucessoGeral);
        dados.put("dataConsulta", LocalDate.now());
        
        // Adicionar detalhes por endpoint
        Map<String, Object> endpointsDetalhes = new HashMap<>();
        for (String operacao : volumeRequisicoes.keySet()) {
            Map<String, Object> detalheEndpoint = new HashMap<>();
            long requisicoes = volumeRequisicoes.get(operacao).get();
            long sucesso = volumeSucesso.getOrDefault(operacao, new AtomicLong(0)).get();
            List<Long> tempos = temposRespostaMs.getOrDefault(operacao, Collections.emptyList());
            
            detalheEndpoint.put("requisicoes", requisicoes);
            detalheEndpoint.put("sucesso", sucesso);
            detalheEndpoint.put("percentualSucesso", requisicoes > 0 ? (double) sucesso / requisicoes : 0.0);
            
            if (!tempos.isEmpty()) {
                detalheEndpoint.put("tempoMedio", tempos.stream().mapToLong(Long::longValue).average().orElse(0.0));
                detalheEndpoint.put("tempoMinimo", tempos.stream().mapToLong(Long::longValue).min().orElse(0L));
                detalheEndpoint.put("tempoMaximo", tempos.stream().mapToLong(Long::longValue).max().orElse(0L));
            }
            
            endpointsDetalhes.put(operacao, detalheEndpoint);
        }
        dados.put("endpoints", endpointsDetalhes);
        
        return dados;
    }

    /**
     * Limpa os dados de telemetria
     */
    public void limparDados() {
        volumeRequisicoes.clear();
        tempoTotalRespostaNanos.clear();
        volumeSucesso.clear();
        temposRespostaMs.clear();
    }
}
