package com.cliente.projeto.crudpb.exception;

import com.cliente.projeto.crudpb.dto.ErroDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroDTO> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Coleta todas as mensagens de erro dos campos
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Erro de validação na rota {}: {}", request.getRequestURI(), mensagem);

        ErroDTO erro = new ErroDTO(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                mensagem,
                request.getRequestURI()
        );
        return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();

        log.error("Erro inesperado [ID: {}] na rota {}: {}", correlationId, request.getRequestURI(), ex.getMessage(), ex);

        String mensagemSegura = "Ocorreu um erro inesperado no sistema. Por favor, tente novamente. ID da falha: " + correlationId;

        ErroDTO erro = new ErroDTO(
                Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno",
                mensagemSegura,
                request.getRequestURI()
        );
        return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}