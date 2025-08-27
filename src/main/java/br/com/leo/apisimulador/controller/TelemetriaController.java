package br.com.leo.apisimulador.controller;

import br.com.leo.apisimulador.config.geral.TimeZoneConfig;
import br.com.leo.apisimulador.dto.telemetria.TelemetriaResponseDTO;
import br.com.leo.apisimulador.service.TelemetriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@Tag(name = "2 - Monitoramento", description = "📊 Telemetria, métricas e monitoramento da aplicação (uso interno)")
@RestController
@RequestMapping("/monitoramento")
public class TelemetriaController {

    private final TelemetriaService telemetria;

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
                dataReferencia = (dataReferencia != null) ? dataReferencia : TimeZoneConfig.today();
                TelemetriaResponseDTO telemetriaFormatada = telemetria.obterTelemetriaFormatada(dataReferencia);
                
                return ResponseEntity.ok(telemetriaFormatada);
    }

}