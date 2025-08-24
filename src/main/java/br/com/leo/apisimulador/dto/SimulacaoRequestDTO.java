package br.com.leo.apisimulador.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SimulacaoRequestDTO(
    @NotNull @jakarta.validation.constraints.DecimalMin("200.00") BigDecimal valorDesejado,
    @NotNull @jakarta.validation.constraints.Min(1) Integer prazo
) {}