package br.com.leo.apisimulador.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulacaoGetResponse(
    Long idSimulacao,
    Integer produtoId,
    String descricaoProduto,
    BigDecimal valorDesejado,
    Integer prazo,
    LocalDate dataReferencia,
    String resultadoJson
) {}