package br.com.leo.apisimulador.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    String message,
    String code,
    LocalDateTime timestamp,
    List<String> details
)   {
        public static ErrorResponse of(String message, String code, String[] details) {
            return new ErrorResponse(
                    message,
                    code,
                    LocalDateTime.now(),
                    List.of(details));
        }
    }