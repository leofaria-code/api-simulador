package br.com.leo.apisimulador.controller;

import br.com.leo.apisimulador.dto.SimulacaoRequest;
import br.com.leo.apisimulador.dto.SimulacaoResponse;
import br.com.leo.apisimulador.model.h2.Simulacao;
import br.com.leo.apisimulador.repository.h2.SimulacaoRepository;
import br.com.leo.apisimulador.service.SimulacaoService;
import br.com.leo.apisimulador.service.TelemetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para os endpoints da API de simulação de crédito.
 */
@RestController
@RequestMapping("/simulacoes")
public class SimulacaoController {
    
    private final SimulacaoService simulacaoService;
    private final SimulacaoRepository simulacaoRepository;
    private final TelemetryService telemetryService;
    
    @Autowired
    public SimulacaoController(SimulacaoService simulacaoService,
                               SimulacaoRepository simulacaoRepository,
                               TelemetryService telemetryService) {
        this.simulacaoService = simulacaoService;
        this.simulacaoRepository = simulacaoRepository;
        this.telemetryService = telemetryService;
    }
    
    /**
     * Endpoint para simular uma operação de crédito e persistir o resultado localmente.
     * @param request Os parâmetros da simulação.
     * @return A resposta da simulação com os resultados.
     */
    @PostMapping
    public ResponseEntity<SimulacaoResponse> simular(@RequestBody SimulacaoRequest request) {
        long startTime = System.nanoTime();
        SimulacaoResponse simulacaoResponse = simulacaoService.simular(request);
        long endTime = System.nanoTime();
        telemetryService.recordResponseTime("POST /simulacoes", Duration.ofNanos(endTime - startTime));
        
        return ResponseEntity.ok(simulacaoResponse);
    }
    
    /**
     * Endpoint para retornar todas as simulações realizadas.
     * @return Uma lista de todas as simulações salvas localmente.
     */
    @GetMapping
    public ResponseEntity<List<Simulacao>> getAllSimulacoes() {
        long startTime = System.nanoTime();
        List<Simulacao> simulacoes = simulacaoRepository.findAll();
        long endTime = System.nanoTime();
        telemetryService.recordResponseTime("GET /simulacoes", Duration.ofNanos(endTime - startTime));
        return ResponseEntity.ok(simulacoes);
    }
    
    /**
     * Endpoint para retornar os valores simulados para cada produto em cada dia.
     * @return Uma lista de mapas com dados agregados.
     */
//    @GetMapping("/agregado-por-dia-e-produto/@date")
//    public ResponseEntity<List<Map<String, Object>>> getSimulacoesPorDiaEProduto(@RequestParameter date) {
//        long startTime = System.nanoTime();
//        // A implementação desta lógica depende de um método de repositório personalizado.
//        // Você precisaria adicionar o seguinte método em SimulacaoRepository.java:
//        // @Query("SELECT ...")
//        // List<Map<String, Object>> findSimulacoesPorProdutoEData();
//        List<Map<String, Object>> result = simulacaoRepository.findSimulacoesPorProdutoEData();
//        long endTime = System.nanoTime();
//        telemetryService.recordResponseTime("GET /simulacoes/agregado-por-dia-e-produto", Duration.ofNanos(endTime - startTime));
//        return ResponseEntity.ok(result);
//    }
    
    /**
     * Endpoint para retornar dados de telemetria (volume e tempo de resposta).
     * @return Um mapa com os dados de telemetria.
     */
    @GetMapping("/telemetria")
    public ResponseEntity<Map<String, Object>> getTelemetryData() {
        return ResponseEntity.ok(telemetryService.getTelemetryData());
    }
}