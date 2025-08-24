package br.com.leo.apisimulador.dto;

import java.util.List;

import br.com.leo.apisimulador.enums.TipoSimulacaoEnum;

public record ResultadoSimulacaoDTO(
        TipoSimulacaoEnum tipo,
        List<ParcelaDTO> parcelas) {
}