package br.com.leo.apisimulador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoSimulacao {
    
    @JsonProperty("tipo")
    private TipoSimulacao tipo;
    
    @JsonProperty("parcelas")
    private List<Parcela> parcelas;
}