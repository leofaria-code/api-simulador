package br.com.leo.apisimulador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para representar a resposta completa de telemetria com todos os endpoints.
 */
public record TelemetriaResponseDTO(
        @JsonProperty("dataReferencia")
        LocalDate dataReferencia,
        
        @JsonProperty("listaEndpoints")
        List<TelemetriaEndpointDTO> listaEndpoints
) {
}