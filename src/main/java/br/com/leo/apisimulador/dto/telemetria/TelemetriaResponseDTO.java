package br.com.leo.apisimulador.dto.telemetria;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para representar a resposta completa de telemetria com todos os
 * endpoints.
 */
@Schema(description = "Dados de telemetria consolidados da aplicação")
public record TelemetriaResponseDTO(
                @Schema(description = "Data de referência para os dados de telemetria", example = "2024-01-15") @JsonProperty("dataReferencia") LocalDate dataReferencia,

                @Schema(description = "Informações sobre a aplicação") @JsonProperty("infoAplicacao") TelemetriaInfoAplicacaoDTO infoAplicacao,

                @Schema(description = "Lista de métricas por endpoint da aplicação") @JsonProperty("listaEndpoints") List<TelemetriaEndpointDTO> listaEndpoints) {
}