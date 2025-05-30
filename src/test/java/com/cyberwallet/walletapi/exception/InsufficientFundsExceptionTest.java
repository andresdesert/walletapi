package com.cyberwallet.walletapi.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsufficientFundsExceptionTest {

    @Test
    @DisplayName("🔴 InsufficientFundsException contiene mensaje, balance y monto solicitados")
    void shouldContainExpectedFields() {
        // Arrange
        String expectedMessage = "Saldo insuficiente para completar la operación.";
        double availableBalance = 100.0;
        double requestedAmount = 150.0;

        // Act
        InsufficientFundsException exception =
                new InsufficientFundsException(expectedMessage, availableBalance, requestedAmount);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(availableBalance, exception.getAvailableBalance());
        assertEquals(requestedAmount, exception.getRequestedAmount());
    }
}
