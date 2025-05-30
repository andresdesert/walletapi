package com.cyberwallet.walletapi.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotFoundExceptionTest {

    @Test
    @DisplayName("🟢 Debe crear UserNotFoundException con mensaje personalizado")
    void shouldCreateWithCustomMessage() {
        String mensaje = "Usuario no encontrado con ID 42";
        UserNotFoundException ex = new UserNotFoundException(mensaje);

        assertThat(ex).isInstanceOf(UserNotFoundException.class);
        assertThat(ex.getMessage()).isEqualTo(mensaje);
    }
}