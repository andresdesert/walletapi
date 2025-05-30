package com.cyberwallet.walletapi.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/fake-uri");
    }

    @Test
    @DisplayName("🧪 UserNotFoundException devuelve 404")
    void shouldHandleUserNotFoundException() {
        UserNotFoundException ex = new UserNotFoundException("Usuario no encontrado");

        ResponseEntity<ApiError> response = handler.handleUserNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Usuario no encontrado");
    }

    @Test
    @DisplayName("🧪 EmailAlreadyUsedException devuelve 409")
    void shouldHandleEmailAlreadyUsedException() {
        EmailAlreadyUsedException ex = new EmailAlreadyUsedException("test@example.com");

        ResponseEntity<ApiError> response = handler.handleEmailAlreadyUsed(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("El email ya está registrado");
    }

    @Test
    @DisplayName("🧪 InvalidCredentialsException devuelve 401")
    void shouldHandleInvalidCredentialsException() {
        InvalidCredentialsException ex = new InvalidCredentialsException();

        ResponseEntity<ApiError> response = handler.handleInvalidCredentials(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Credenciales inválidas");
    }

    @Test
    @DisplayName("🧪 InvalidTokenException devuelve 401")
    void shouldHandleInvalidTokenException() {
        InvalidTokenException ex = new InvalidTokenException("mockedToken");

        ResponseEntity<ApiError> response = handler.handleInvalidToken(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Token inválido");
    }

    @Test
    @DisplayName("🧪 AuthenticationException devuelve 401")
    void shouldHandleAuthenticationException() {
        AuthenticationException ex = new AuthenticationException("No autenticado") {};

        ResponseEntity<ApiError> response = handler.handleAuthenticationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Acceso no autorizado");
    }

    @Test
    @DisplayName("🧪 MethodArgumentNotValidException devuelve 400 con detalles")
    void shouldHandleValidationExceptions() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("object", "firstname", "El nombre es obligatorio"),
                new FieldError("object", "email", "Formato de email inválido")
        ));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiError> response = handler.handleMethodArgumentNotValid(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("firstname: El nombre es obligatorio");
        assertThat(response.getBody().getMessage()).contains("email: Formato de email inválido");
    }

    @Test
    @DisplayName("🧪 Exception genérica devuelve 500")
    void shouldHandleGenericException() {
        Exception ex = new Exception("Falla genérica");

        ResponseEntity<ApiError> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Ha ocurrido un error inesperado.");
    }
}
