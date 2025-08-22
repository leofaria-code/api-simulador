package br.com.leo.apisimulador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parcela {
    
    @JsonProperty("numero")
    private Integer numero;
    
    @JsonProperty("valorAmortizacao")
    private Double valorAmortizacao;
    
    @JsonProperty("valorJuros")
    private Double valorJuros;
    
    @JsonProperty("valorPrestacao")
    private Double valorPrestacao;
}