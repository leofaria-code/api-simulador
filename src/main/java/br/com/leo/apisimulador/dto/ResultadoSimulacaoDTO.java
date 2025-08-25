package br.com.leo.apisimulador.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import br.com.leo.apisimulador.enums.TipoSimulacaoEnum;

@Schema(description = "Resultado da simulação para um sistema de amortização específico")
public record ResultadoSimulacaoDTO(
                @Schema(description = "Tipo do sistema de amortização", example = "SAC") TipoSimulacaoEnum tipo,

                @Schema(description = "Lista detalhada de todas as parcelas do financiamento") List<ParcelaDTO> parcelas) {
}