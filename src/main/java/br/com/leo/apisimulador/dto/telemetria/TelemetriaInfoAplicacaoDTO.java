package br.com.leo.apisimulador.dto.telemetria;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar informações sobre a aplicação.
 */
@Schema(description = "Informações sobre a aplicação")
public record TelemetriaInfoAplicacaoDTO(
    @Schema(description = "Nome da aplicação", example = "api-simulador") 
    @JsonProperty("nome") String nome,

    @Schema(description = "Versão da aplicação", example = "1.0.0") 
    @JsonProperty("versao") String versao) {
    
    // Compact constructor to set default values
    public TelemetriaInfoAplicacaoDTO {
        nome = nome == null ? "api-simulador" : nome;
        versao = versao == null ? "1.0.0" : versao;
    }
}