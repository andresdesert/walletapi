package com.cyberwallet.walletapi.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAuthenticationEntryPointTest {

    private CustomAuthenticationEntryPoint entryPoint;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        entryPoint = new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Test
    @DisplayName("🧪 Retorna 401 con JSON válido cuando no hay autenticación")
    void shouldReturn401JsonError() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/protected");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticationException exception = new AuthenticationException("Acceso denegado") {};

        entryPoint.commence(request, response, exception);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json");

        String json = response.getContentAsString();
        assertThat(json).contains("\"statusCode\":401");
        assertThat(json).contains("\"errorCode\":\"INVALID_TOKEN\"");
        assertThat(json).contains("\"message\":\"Acceso no autorizado\"");
        assertThat(json).contains("\"timestamp\":");
        assertThat(json).contains("\"path\":\"/api/v1/protected\"");

        // Deserializamos el JSON para validar todos los campos
        ApiError apiError = objectMapper.readValue(json, ApiError.class);
        assertThat(apiError.getStatusCode()).isEqualTo(401);
        assertThat(apiError.getErrorCode().name()).isEqualTo("INVALID_TOKEN");
        assertThat(apiError.getMessage()).isEqualTo("Acceso no autorizado");
        assertThat(apiError.getPath()).isEqualTo("/api/v1/protected");
        assertThat(apiError.getTimestamp()).isNotNull();
    }
}
