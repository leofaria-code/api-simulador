package br.com.leo.apisimulador.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

import br.com.leo.apisimulador.config.geral.TimeZoneConfig;

@Schema(description = "Resposta de erro padronizada da API")
public record ErrorResponseDTO(
        @Schema(description = "Mensagem de erro principal", example = "Dados inválidos fornecidos") String message,

        @Schema(description = "Código de erro interno", example = "VALIDATION_ERROR") String code,

        @Schema(description = "Timestamp do erro (horário de São Paulo)", example = "2025-08-25T14:30:00") LocalDateTime timestamp,

        @Schema(description = "Lista de detalhes específicos do erro") List<String> details) {
    public static ErrorResponseDTO of(String message, String code, String[] details) {
        return new ErrorResponseDTO(
                message,
                code,
                TimeZoneConfig.now(),
                List.of(details));
    }
}