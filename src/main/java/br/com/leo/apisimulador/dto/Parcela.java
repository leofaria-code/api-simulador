package br.com.leo.apisimulador.dto;

import java.math.BigDecimal;

public record Parcela(
    int numero,
    BigDecimal valorAmortizacao,
    BigDecimal valorJuros,
    BigDecimal valorPrestacao
) {}