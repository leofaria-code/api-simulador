package br.com.leo.apisimulador.dto.telemetria;

import java.time.LocalDate;
import java.util.List;

public record VolumeSimuladoResponseDTO(
    LocalDate dataReferencia,
    List<VolumeSimuladoProdutoDTO> simulacoes
) {}
