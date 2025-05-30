package com.cyberwallet.walletapi.controller;

import com.cyberwallet.walletapi.config.SecurityConfig;
import com.cyberwallet.walletapi.dto.auth.AuthenticationRequest;
import com.cyberwallet.walletapi.dto.auth.AuthenticationResponse;
import com.cyberwallet.walletapi.dto.auth.RegisterRequest;
import com.cyberwallet.walletapi.exception.CustomAuthenticationEntryPoint;
import com.cyberwallet.walletapi.exception.EmailAlreadyUsedException;
import com.cyberwallet.walletapi.exception.InvalidCredentialsException;
import com.cyberwallet.walletapi.repository.UserRepository;
import com.cyberwallet.walletapi.security.JwtService;
import com.cyberwallet.walletapi.security.UserDetailsServiceImpl;
import com.cyberwallet.walletapi.service.impl.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("🟢 Registro exitoso devuelve token")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("test@user.com")
                .password("secure123")
                .build();

        AuthenticationResponse response = new AuthenticationResponse("mocked-jwt-token");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    @DisplayName("🔴 Registro falla por email duplicado")
    void shouldFailRegisterWhenEmailUsed() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("duplicate@user.com")
                .password("secure123")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new EmailAlreadyUsedException("El email ya está registrado: duplicate@user.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("El email ya está registrado")));
    }

    @Test
    @DisplayName("🟢 Autenticación exitosa devuelve token")
    void shouldAuthenticateSuccessfully() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("test@user.com", "secure123");
        AuthenticationResponse response = new AuthenticationResponse("valid-token");

        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("valid-token"));
    }

    @Test
    @DisplayName("🔴 Login con credenciales inválidas")
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("wrong@user.com", "badpass");

        when(authService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new InvalidCredentialsException("Credenciales inválidas. Por favor revisá tu email y contraseña."));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Credenciales inválidas")));
    }

    @Test
    @DisplayName("🔴 Validación de campos fallida")
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("") // Forzar error @NotBlank
                .lastname("User")
                .email("invalid-email-format")
                .password("123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message", containsString("firstname: El nombre es obligatorio")))
                .andExpect(jsonPath("$.message", containsString("email: Formato de email inválido")))
                .andExpect(jsonPath("$.message", containsString("password: La contraseña debe tener al menos 6 caracteres")));
    }
}
