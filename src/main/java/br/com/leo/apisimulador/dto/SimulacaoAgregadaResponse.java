package br.com.leo.apisimulador.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulacaoAgregadaResponse(
    Integer produtoId,
    String descricaoProduto,
    LocalDate dataReferencia,
    Long quantidadeSimulacoes,
    BigDecimal valorTotalSimulado,
    BigDecimal valorMedioSimulado,
    Integer prazoMedio
) {}