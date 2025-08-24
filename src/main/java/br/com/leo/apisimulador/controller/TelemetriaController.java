package br.com.leo.apisimulador.controller;

import br.com.leo.apisimulador.dto.TelemetriaResponseDTO;
import br.com.leo.apisimulador.service.TelemetriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Monitoramento", description = "Endpoints para monitoramento da aplicação")
@RestController
@RequestMapping("/monitoramento")
public class TelemetriaController {

    private final TelemetriaService telemetria;

    public TelemetriaController(TelemetriaService telemetria) {
        this.telemetria = telemetria;
    }
    
    @Operation(
        summary = "Obter telemetria formatada dos endpoints de simulação", 
        description = "Retorna dados de telemetria estruturados para cada endpoint da SimulacaoController, incluindo volume de requisições, tempos de resposta e percentual de sucesso"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Telemetria obtida com sucesso", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TelemetriaResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/telemetria")
    public ResponseEntity<TelemetriaResponseDTO> obterTelemetria(
            @Parameter(description = "Data de referência para a telemetria (formato: YYYY-MM-DD). Se não informada, usa a data atual.")
            @RequestParam(value = "dataReferencia", required = false) LocalDate dataReferencia) {
        
        // Se não foi informada a data, usa a data atual
        if (dataReferencia == null) {
            dataReferencia = LocalDate.now();
        }
        
        TelemetriaResponseDTO telemetriaFormatada = telemetria.obterTelemetriaFormatada(dataReferencia);
        return ResponseEntity.ok(telemetriaFormatada);
    }
}