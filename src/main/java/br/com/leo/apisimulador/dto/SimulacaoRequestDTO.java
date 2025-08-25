package br.com.leo.apisimulador.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Dados de entrada para realizar uma simulação de empréstimo")
public record SimulacaoRequestDTO(
        @Schema(description = "Valor desejado para o empréstimo", example = "10000.00", minimum = "200.00") @NotNull @jakarta.validation.constraints.DecimalMin("200.00") BigDecimal valorDesejado,

        @Schema(description = "Prazo para pagamento em meses", example = "24", minimum = "1") @NotNull @jakarta.validation.constraints.Min(1) Integer prazo) {
}