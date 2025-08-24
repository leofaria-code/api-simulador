package br.com.leo.apisimulador.config;

import br.com.leo.apisimulador.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Handler global para tratamento de exceções da aplicação.
 * Temporariamente comentado devido a conflito com SpringDoc
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções gerais da aplicação.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        log.error("Erro interno da aplicação: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                "Erro interno da aplicação",
                "INTERNAL_ERROR",
                new String[] { ex.getMessage() });

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    /**
     * Trata exceções de argumento inválido.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.error("Argumento inválido: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                "Dados inválidos",
                "INVALID_ARGUMENT",
                new String[] { ex.getMessage() });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Trata exceções gerais não capturadas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Erro inesperado: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                "Erro interno do servidor",
                "UNEXPECTED_ERROR",
                new String[] { "Ocorreu um erro inesperado. Tente novamente mais tarde." });

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
