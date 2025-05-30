package com.cyberwallet.walletapi.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/fake-uri");
        webRequest = new ServletWebRequest(servletRequest);
    }

    @Test
    @DisplayName("🧪 UserNotFoundException devuelve 404")
    void shouldHandleUserNotFoundException() {
        UserNotFoundException ex = new UserNotFoundException("Usuario no encontrado");

        ResponseEntity<ApiError> response = handler.handleCustom(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Usuario no encontrado");
    }

    @Test
    @DisplayName("🧪 EmailAlreadyUsedException devuelve 409")
    void shouldHandleEmailAlreadyUsedException() {
        EmailAlreadyUsedException ex = new EmailAlreadyUsedException("Email ya está en uso");

        ResponseEntity<ApiError> response = handler.handleCustom(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Email ya está en uso");
    }

    @Test
    @DisplayName("🧪 InvalidCredentialsException devuelve 401")
    void shouldHandleInvalidCredentialsException() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Credenciales inválidas");

        ResponseEntity<ApiError> response = handler.handleCustom(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Credenciales inválidas");
    }

    @Test
    @DisplayName("🧪 InvalidTokenException devuelve 401")
    void shouldHandleInvalidTokenException() {
        InvalidTokenException ex = new InvalidTokenException("Token inválido");

        ResponseEntity<ApiError> response = handler.handleCustom(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Token inválido");
    }

    @Test
    @DisplayName("🧪 AuthenticationException devuelve 401")
    void shouldHandleAuthenticationException() {
        AuthenticationException ex = new AuthenticationException("No autenticado") {};
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/fake-uri");

        ResponseEntity<ApiError> response = handler.handleAuthError(ex, (jakarta.servlet.http.HttpServletRequest) servletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("No autenticado");
        assertThat(response.getBody().getStatusCode()).isEqualTo(401);
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

        ResponseEntity<ApiError> response = handler.handleValidation(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("firstname: El nombre es obligatorio");
        assertThat(response.getBody().getMessage()).contains("email: Formato de email inválido");
    }
    @Test
    @DisplayName("🧪 Exception genérica devuelve 500")
    void shouldHandleGenericException() {
        Exception ex = new Exception("Falla genérica");

        ResponseEntity<ApiError> response = handler.handleAll(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Falla genérica");
        assertThat(response.getBody().getStatusCode()).isEqualTo(500);
    }

}
