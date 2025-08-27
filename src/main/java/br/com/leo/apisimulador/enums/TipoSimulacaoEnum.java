package br.com.leo.apisimulador.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonValue;

@Schema(description = "Tipos de sistemas de amortização disponíveis")
public enum TipoSimulacaoEnum {

    @Schema(description = "Sistema de Amortização Constante - parcelas decrescentes")
    SAC("SAC"),

    @Schema(description = "Sistema Francês de Amortização - parcelas fixas")
    PRICE("PRICE");

    private final String descricao;

    TipoSimulacaoEnum(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }
}