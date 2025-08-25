package br.com.leo.apisimulador.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta de erro padronizada da API")
public record ErrorResponseDTO(
        @Schema(description = "Mensagem de erro principal", example = "Dados inválidos fornecidos") String message,

        @Schema(description = "Código de erro interno", example = "VALIDATION_ERROR") String code,

        @Schema(description = "Timestamp do erro", example = "2024-01-15T10:30:00") LocalDateTime timestamp,

        @Schema(description = "Lista de detalhes específicos do erro") List<String> details) {
    public static ErrorResponseDTO of(String message, String code, String[] details) {
        return new ErrorResponseDTO(
                message,
                code,
                LocalDateTime.now(),
                List.of(details));
    }
}