package br.com.leo.apisimulador.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

@Schema(description = "Detalhes de uma parcela do financiamento")
public record ParcelaDTO(
                @Schema(description = "Número da parcela", example = "1") int numero,

                @Schema(description = "Valor da amortização do principal", example = "416.67") @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00") BigDecimal valorAmortizacao,

                @Schema(description = "Valor dos juros da parcela", example = "150.00") @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00") BigDecimal valorJuros,

                @Schema(description = "Valor total da prestação (amortização + juros)", example = "566.67") @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.00") BigDecimal valorPrestacao) {
}