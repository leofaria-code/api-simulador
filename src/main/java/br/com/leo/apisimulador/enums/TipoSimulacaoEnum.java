package br.com.leo.apisimulador.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoSimulacaoEnum {
    
    SAC("SAC"),
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