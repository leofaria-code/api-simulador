package br.com.leo.apisimulador.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoSimulacao {
    
    SAC("SAC"),
    PRICE("PRICE");
    
    private final String descricao;
    
    TipoSimulacao(String descricao) {
        this.descricao = descricao;
    }
    
    @JsonValue
    public String getDescricao() {
        return descricao;
    }
}