package com.cyberwallet.walletapi.dto.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationRequestTest {

    @Test
    @DisplayName("🧪 Crear AuthenticationRequest con valores válidos")
    void shouldCreateAuthenticationRequest() {
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "securePassword");

        assertThat(request.getEmail()).isEqualTo("user@example.com");
        assertThat(request.getPassword()).isEqualTo("securePassword");
    }

    @Test
    @DisplayName("🧪 AuthenticationRequest toString incluye email pero oculta contraseña")
    void shouldCallToString() {
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "securePassword");
        String result = request.toString();

        // Valida que el email está presente
        assertThat(result).contains("email='user@example.com'");
        // Valida que la contraseña no aparece en texto plano
        assertThat(result).doesNotContain("securePassword");
        // Valida que la contraseña está enmascarada
        assertThat(result).contains("password='********'");
    }
}
