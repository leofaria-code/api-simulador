package br.com.leo.apisimulador.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulacaoGetResponseDTO(
    Long idSimulacao,
    Integer produtoId,
    String descricaoProduto,
    
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00")
    BigDecimal valorDesejado,
    
    Integer prazo,
    LocalDate dataReferencia,
    String resultadoJson
) {}