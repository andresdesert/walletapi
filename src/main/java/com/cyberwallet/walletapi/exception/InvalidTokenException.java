package com.cyberwallet.walletapi.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("El token proporcionado no es válido: " + token);
    }
}

