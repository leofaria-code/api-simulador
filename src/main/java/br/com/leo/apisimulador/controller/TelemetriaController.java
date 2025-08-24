package br.com.leo.apisimulador.controller;

import br.com.leo.apisimulador.service.TelemetriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Monitoramento", description = "Endpoints para monitoramento da aplicação")
@RestController
@RequestMapping("/monitoramento")
public class TelemetriaController {

    private final TelemetriaService telemetria;

    public TelemetriaController(TelemetriaService telemetria) {
        this.telemetria = telemetria;
    }

    @Operation(summary = "Obter estatísticas gerais", description = "Retorna métricas de uso e performance de todos os endpoints")
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        return ResponseEntity.ok(telemetria.obterEstatisticas());
    }
    
    @Operation(summary = "Obter telemetria detalhada", description = "Retorna dados detalhados de volume e tempo de resposta por endpoint")
    @GetMapping("/telemetria")
    public ResponseEntity<Map<String, Object>> obterDadosTelemetria() {
        return ResponseEntity.ok(telemetria.obterDadosTelemetria());
    }
    
    @Operation(summary = "Obter métricas de performance", description = "Retorna indicadores de performance da API")
    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> obterMetricasPerformance() {
        Map<String, Object> metricas = telemetria.obterDadosTelemetria();
        // Podemos adicionar métricas de performance mais específicas aqui
        return ResponseEntity.ok(metricas);
    }
}