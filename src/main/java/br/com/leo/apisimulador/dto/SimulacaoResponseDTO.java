package br.com.leo.apisimulador.dto;

import java.math.BigDecimal;
import java.util.List;

public record SimulacaoResponseDTO(
                Long idSimulacao,
                int codigoProduto,
                String descricaoProduto,
                BigDecimal taxaJuros,
                List<ResultadoSimulacaoDTO> resultadosSimulacao) {
}