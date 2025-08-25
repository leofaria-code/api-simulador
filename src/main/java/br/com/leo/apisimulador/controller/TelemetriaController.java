package br.com.leo.apisimulador.controller;

import br.com.leo.apisimulador.dto.TelemetriaResponseDTO;
import br.com.leo.apisimulador.service.TelemetriaService;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "Monitoramento", description = "Endpoints para monitoramento da aplicação")
@RestController
@RequestMapping("/monitoramento")
public class TelemetriaController {

    private final TelemetriaService telemetria;

    @Autowired(required = false)
    private EventHubProducerClient eventHubProducerClient;

    public TelemetriaController(TelemetriaService telemetria) {
        this.telemetria = telemetria;
    }

    @Operation(summary = "Obter telemetria formatada dos endpoints de simulação", description = "Retorna dados de telemetria estruturados para cada endpoint da SimulacaoController, incluindo volume de requisições, tempos de resposta e percentual de sucesso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Telemetria obtida com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TelemetriaResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/telemetria")
    public ResponseEntity<TelemetriaResponseDTO> obterTelemetria(
            @Parameter(description = "Data de referência para a telemetria (formato: YYYY-MM-DD). Se não informada, usa a data atual.") @RequestParam(value = "dataReferencia", required = false) LocalDate dataReferencia) {

        // Se não foi informada a data, usa a data atual
        if (dataReferencia == null) {
            dataReferencia = LocalDate.now();
        }

        TelemetriaResponseDTO telemetriaFormatada = telemetria.obterTelemetriaFormatada(dataReferencia);
        return ResponseEntity.ok(telemetriaFormatada);
    }

    @Operation(summary = "Testar conexão com Azure Event Hub", description = "Envia uma mensagem de teste para o Azure Event Hub para verificar se a integração está funcionando")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teste realizado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro na conexão com Event Hub")
    })
    @PostMapping("/test-eventhub")
    public ResponseEntity<Map<String, Object>> testarEventHub() {

        if (eventHubProducerClient == null) {
            log.warn("❌ EventHubProducerClient não está configurado");
            return ResponseEntity.ok(Map.of(
                    "status", "DISABLED",
                    "message", "Azure Event Hub está desabilitado ou não configurado",
                    "timestamp", LocalDateTime.now()));
        }

        try {
            String mensagemTeste = String.format("""
                    {
                        "tipo": "TESTE_CONEXAO",
                        "timestamp": "%s",
                        "origem": "api-simulador",
                        "dados": {
                            "endpoint": "POST /monitoramento/test-eventhub",
                            "descricao": "Teste de conectividade com Azure Event Hub"
                        }
                    }
                    """, LocalDateTime.now());

            log.info("🧪 Enviando mensagem de teste para Azure Event Hub...");
            eventHubProducerClient.send(List.of(new EventData(mensagemTeste)));
            log.info("✅ Mensagem de teste enviada com sucesso!");

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Mensagem de teste enviada com sucesso para Azure Event Hub",
                    "timestamp", LocalDateTime.now(),
                    "eventhub_configured", true));

        } catch (Exception e) {
            log.error("❌ Erro ao testar Event Hub: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                    "status", "ERROR",
                    "message", "Erro ao conectar com Azure Event Hub: " + e.getMessage(),
                    "timestamp", LocalDateTime.now(),
                    "eventhub_configured", true,
                    "error_details", e.getClass().getSimpleName()));
        }
    }

    @Operation(summary = "Verificar status do Azure Event Hub", description = "Retorna informações sobre a configuração do Azure Event Hub")
    @GetMapping("/eventhub-status")
    public ResponseEntity<Map<String, Object>> verificarStatusEventHub() {

        boolean eventhubConfigurado = eventHubProducerClient != null;

        Map<String, Object> status = Map.of(
                "eventhub_enabled", eventhubConfigurado,
                "timestamp", LocalDateTime.now(),
                "message",
                eventhubConfigurado ? "Azure Event Hub está configurado e pronto para uso"
                        : "Azure Event Hub não está configurado ou está desabilitado",
                "endpoint_test", "/monitoramento/test-eventhub",
                "configuration_check", Map.of(
                        "bean_configured", eventhubConfigurado,
                        "profile_active", eventhubConfigurado ? "Event Hub habilitado" : "Event Hub desabilitado"));

        return ResponseEntity.ok(status);
    }
}