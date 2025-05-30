package com.cyberwallet.walletapi.dto.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationResponseTest {

    @Test
    @DisplayName("🧪 Crear AuthenticationResponse con token")
    void shouldCreateAuthenticationResponse() {
        AuthenticationResponse response = new AuthenticationResponse("dummyToken123");
        assertThat(response.getToken()).isEqualTo("dummyToken123");
    }

    @Test
    @DisplayName("🧪 AuthenticationResponse toString no lanza excepción")
    void shouldCallToString() {
        AuthenticationResponse response = new AuthenticationResponse("dummyToken123");
        assertThat(response.toString()).contains("dummyToken123");
    }
}
