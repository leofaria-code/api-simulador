package br.com.leo.apisimulador.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SimulacaoRequest {
    @NotNull
    private BigDecimal valorDesejado;
    @NotNull
    private Integer prazo;
}

