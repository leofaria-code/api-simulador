package br.com.leo.apisimulador.dto;

import java.util.List;

public record ResultadoSimulacao(
    TipoSimulacao tipo,
    List<Parcela> parcelas
) {}