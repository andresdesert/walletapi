package com.cyberwallet.walletapi.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super("Credenciales inválidas. Por favor revisá tu email y contraseña.");
    }
}
