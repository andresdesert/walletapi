package com.cyberwallet.walletapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 401 - No autenticado
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthError(AuthenticationException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    // 403 - Sin permisos
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, request.getRequestURI());
    }

    // 400 - Validación de campos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        ApiError error = ApiError.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .error("Datos inválidos")
                .message(mensaje)
                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Excepciones personalizadas
    @ExceptionHandler({
            UserNotFoundException.class,
            EmailAlreadyUsedException.class,
            InvalidCredentialsException.class,
            InvalidTokenException.class
    })
    public ResponseEntity<ApiError> handleCustom(RuntimeException ex, WebRequest request) {
        HttpStatus status = switch (ex.getClass().getSimpleName()) {
            case "UserNotFoundException" -> HttpStatus.NOT_FOUND;
            case "EmailAlreadyUsedException" -> HttpStatus.CONFLICT;
            case "InvalidCredentialsException", "InvalidTokenException" -> HttpStatus.UNAUTHORIZED;
            default -> HttpStatus.BAD_REQUEST;
        };

        return buildErrorResponse(ex, status, ((ServletWebRequest) request).getRequest().getRequestURI());
    }

    // Fallback general
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
        log.error("🔴 [ERROR] {}: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, ((ServletWebRequest) request).getRequest().getRequestURI());
    }

    private ResponseEntity<ApiError> buildErrorResponse(Exception ex, HttpStatus status, String path) {
        ApiError error = ApiError.builder()
                .statusCode(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, status);
    }
}
