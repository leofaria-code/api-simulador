package br.com.leo.apisimulador.dto.telemetria;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar dados de telemetria de um endpoint específico.
 */
public record TelemetriaEndpointDTO(
        @JsonProperty("nomeEndpoint")
        String nomeEndpoint,
        
        @JsonProperty("qtdRequisicoes")
        Long qtdRequisicoes,
        
        @JsonProperty("tempoMedio")
        Long tempoMedio,
        
        @JsonProperty("tempoMinimo")
        Long tempoMinimo,
        
        @JsonProperty("tempoMaximo")
        Long tempoMaximo,
        
        @JsonProperty("percentualSucesso")
        Double percentualSucesso
) {
}