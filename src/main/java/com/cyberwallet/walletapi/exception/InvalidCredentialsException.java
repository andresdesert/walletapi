package com.cyberwallet.walletapi.exception;

/**
 * Excepción que se lanza cuando las credenciales de autenticación son inválidas.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constructor QA-driven con mensaje automático fijo.
     */
    public InvalidCredentialsException() {
        super("Credenciales inválidas. Por favor revisá tu email y contraseña.");
    }
}
