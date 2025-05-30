package com.cyberwallet.walletapi.integration;

import com.cyberwallet.walletapi.dto.auth.AuthenticationRequest;
import com.cyberwallet.walletapi.dto.auth.RegisterRequest;
import com.cyberwallet.walletapi.entity.User;
import com.cyberwallet.walletapi.enums.Role;
import com.cyberwallet.walletapi.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("🟢 Registro real exitoso con DB embebida")
    void shouldRegisterUserWithRealContext() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Integration")
                .lastname("Test")
                .email("integration@test.com")
                .password("secure123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));

        assertThat(userRepository.findByEmail("integration@test.com")).isPresent();
    }

    @Test
    @DisplayName("🟢 Login exitoso devuelve JWT real")
    void shouldLoginSuccessfullyWithRealContext() throws Exception {
        userRepository.save(User.builder()
                .fullName("Test User")
                .email("login@test.com")
                .password(passwordEncoder.encode("secure123"))
                .role(Role.USER)
                .build());

        AuthenticationRequest request = new AuthenticationRequest("login@test.com", "secure123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @DisplayName("🟢 Acceso exitoso a /me con JWT válido")
    void shouldAccessMeWithValidJwt() throws Exception {
        userRepository.save(User.builder()
                .fullName("Test User")
                .email("me@test.com")
                .password(passwordEncoder.encode("secure123"))
                .role(Role.USER)
                .build());

        AuthenticationRequest login = new AuthenticationRequest("me@test.com", "secure123");
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        String jwt = json.get("token").asText();

        mockMvc.perform(get("/api/v1/user/me")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("me@test.com"));
    }

    @Test
    @DisplayName("🔴 Acceso denegado a /me con JWT inválido")
    void shouldFailAccessMeWithInvalidJwt() throws Exception {
        String fakeToken = "ey.invalid.jwt.token.value";

        mockMvc.perform(get("/api/v1/user/me")
                        .header("Authorization", "Bearer " + fakeToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.errorCode").value("INVALID_TOKEN"));
    }

    @Test
    @DisplayName("🔴 Acceso denegado a /me sin JWT")
    void shouldFailAccessMeWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/v1/user/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.errorCode").value("INVALID_TOKEN"));
    }
}
