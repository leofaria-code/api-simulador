package br.com.leo.apisimulador.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        String message,
        String code,
        LocalDateTime timestamp,
        List<String> details) {
    public static ErrorResponseDTO of(String message, String code, String[] details) {
        return new ErrorResponseDTO(
                message,
                code,
                LocalDateTime.now(),
                List.of(details));
    }
}