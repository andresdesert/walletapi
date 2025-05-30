package com.cyberwallet.walletapi.exception;

/**
 * Excepción que se lanza cuando un usuario intenta realizar una operación
 * que requiere más saldo del que dispone.
 */
public class InsufficientFundsException extends RuntimeException {

    private final double availableBalance;
    private final double requestedAmount;

    /**
     * Constructor QA-driven con mensaje automático.
     */
    public InsufficientFundsException(double availableBalance, double requestedAmount) {
        super("Saldo insuficiente para completar la operación.");
        this.availableBalance = availableBalance;
        this.requestedAmount = requestedAmount;
    }

    /**
     * Constructor con mensaje personalizado.
     */
    public InsufficientFundsException(String message, double availableBalance, double requestedAmount) {
        super(message);
        this.availableBalance = availableBalance;
        this.requestedAmount = requestedAmount;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }
}
