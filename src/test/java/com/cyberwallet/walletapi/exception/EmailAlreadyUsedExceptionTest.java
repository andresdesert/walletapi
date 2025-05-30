package com.cyberwallet.walletapi.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailAlreadyUsedExceptionTest {

    @Test
    @DisplayName("🔴 EmailAlreadyUsedException incluye email en el mensaje")
    void shouldIncludeEmailInMessage() {
        String email = "cualquier@correo.com";
        EmailAlreadyUsedException ex = new EmailAlreadyUsedException(email);

        assertThat(ex.getMessage()).contains(email);
    }
}
