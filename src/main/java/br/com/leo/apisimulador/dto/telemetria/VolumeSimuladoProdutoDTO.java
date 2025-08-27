package br.com.leo.apisimulador.dto.telemetria;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

public record VolumeSimuladoProdutoDTO(
    Integer codigoProduto,
    String descricaoProduto,
    Integer quantidadeSimulacoes,
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.0000")
    BigDecimal taxaMediaJuro,
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00")
    BigDecimal valorTotalDesejado,
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00")
    BigDecimal valorTotalCredito,
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00")
    BigDecimal valorMedioPrestacao
) {}
