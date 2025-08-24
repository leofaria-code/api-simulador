package br.com.leo.apisimulador.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulacaoAgregadaResponseDTO(
    Integer produtoId,
    String descricaoProduto,
    LocalDate dataReferencia,
    Long quantidadeSimulacoes,
    
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00")
    BigDecimal valorTotalSimulado,
    
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00")
    BigDecimal valorMedioSimulado,
    
    Integer prazoMedio
) {}