package br.com.leo.apisimulador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimulacaoResponse {

//    @JsonProperty("idSimulacao")
//    private Long idSimulacao;
    
    @JsonProperty("codigoProduto")
    private int codigoProduto;
    
    @JsonProperty("descricaoProduto")
    private String descricaoProduto;
    
    @JsonProperty("taxaJuros")
    private BigDecimal taxaJuros;
    
    @JsonProperty("resultadosSimulacao")
    private List<ResultadoSimulacao> resultadosSimulacao;
    
}