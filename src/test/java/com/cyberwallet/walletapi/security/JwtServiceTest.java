package com.cyberwallet.walletapi.security;

import com.cyberwallet.walletapi.entity.User;
import com.cyberwallet.walletapi.enums.Role;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // ⚠️ Clave real usada en entorno de test (hexadecimal válida)
        String hexSecret = "a3f1c6e9b2478f5d92c49f3ee71349afcbe916d3640ffb3a94a6012ccbbd12b3";
        jwtService.setSecretKeyHex(hexSecret);

        testUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("🟢 Generar token correctamente")
    void shouldGenerateTokenSuccessfully() {
        String token = jwtService.generateToken(testUser);
        assertNotNull(token);
        assertTrue(token.startsWith("ey"), "El token debería empezar con 'ey'");
    }

    @Test
    @DisplayName("🟢 Validar token generado")
    void shouldValidateTokenSuccessfully() {
        String token = jwtService.generateToken(testUser);
        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    @DisplayName("🔴 Detectar token inválido (malformado)")
    void shouldRejectInvalidToken() {
        String malformedToken = "esto.no.es.jwt";
        assertThrows(MalformedJwtException.class, () -> jwtService.isTokenValid(malformedToken, testUser));
    }

    @Test
    @DisplayName("🟢 Extraer username desde el token")
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo(testUser.getEmail());
    }
}
