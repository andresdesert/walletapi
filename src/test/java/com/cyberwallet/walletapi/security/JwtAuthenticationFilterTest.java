package com.cyberwallet.walletapi.security;

import com.cyberwallet.walletapi.dto.auth.RegisterRequest;
import com.cyberwallet.walletapi.entity.User;
import com.cyberwallet.walletapi.enums.Role;
import com.cyberwallet.walletapi.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;
    private JwtService jwtService;
    private UserDetailsServiceImpl userDetailsService;
    private ObjectMapper objectMapper;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userDetailsService = mock(UserDetailsServiceImpl.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService, objectMapper);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("🔴 Token mal formado devuelve 401 con JSON válido")
    void shouldReturn401WhenTokenMalformed() throws Exception {
        // Simula un token mal formado que lanza excepción
        when(request.getHeader("Authorization")).thenReturn("Bearer ey.malformed.token");

        doThrow(new IllegalArgumentException("Token mal formado"))
                .when(jwtService).extractUsername("ey.malformed.token");

        MockHttpServletResponse realResponse = new MockHttpServletResponse();

        filter.doFilterInternal(request, realResponse, filterChain);

        // Verifica que se setee el status 401
        assertThat(realResponse.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(realResponse.getContentType()).isEqualTo("application/json");

        String jsonOutput = realResponse.getContentAsString();
        assertThat(jsonOutput).contains("\"statusCode\":401");
        assertThat(jsonOutput).contains("\"error\":\"Unauthorized\"");
        assertThat(jsonOutput).contains("\"message\":\"JWT inv");
    }
}
