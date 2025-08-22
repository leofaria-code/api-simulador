package br.com.leo.apisimulador.dto;

import java.math.BigDecimal;
import java.util.List;

public record SimulacaoResponse(
    Long idSimulacao,
    int codigoProduto,
    String descricaoProduto,
    BigDecimal taxaJuros,
    List<ResultadoSimulacao> resultadosSimulacao
) {}