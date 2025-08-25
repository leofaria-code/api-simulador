package br.com.leo.apisimulador.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Resposta da simulação contendo os resultados calculados")
public record SimulacaoResponseDTO(
        @Schema(description = "ID único da simulação", example = "123456") Long idSimulacao,

        @Schema(description = "Código do produto financeiro", example = "1") int codigoProduto,

        @Schema(description = "Descrição do produto financeiro", example = "Crédito Pessoal") String descricaoProduto,

        @Schema(description = "Taxa de juros aplicada", example = "0.0015") BigDecimal taxaJuros,

        @Schema(description = "Lista de resultados por sistema de amortização (SAC e PRICE)") List<ResultadoSimulacaoDTO> resultadosSimulacao) {
}