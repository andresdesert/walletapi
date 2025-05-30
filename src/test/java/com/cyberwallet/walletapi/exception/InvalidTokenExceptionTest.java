package com.cyberwallet.walletapi.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidTokenExceptionTest {

    @Test
    @DisplayName("🔴 InvalidTokenException contiene mensaje personalizado")
    void shouldContainExpectedMessage() {
        String token = "abc.def.ghi";
        InvalidTokenException exception = new InvalidTokenException(token);

        assertThat(exception.getMessage())
                .isEqualTo("El token proporcionado no es válido: abc.def.ghi");
    }
}
