package br.com.leo.apisimulador.dto.simulacao;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

/**
 * DTO para resposta resumida de simulação
 */
public record SimulacaoResumoDTO(
        Long idSimulacao,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00") BigDecimal valorDesejado,
        Integer prazo,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00") BigDecimal valorTotalParcelasSAC,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00") BigDecimal valorTotalParcelasPrice) {
}
