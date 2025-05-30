package com.cyberwallet.walletapi.exception;

/**
 * Excepción que se lanza cuando el token JWT proporcionado es inválido o ha expirado.
 */
public class InvalidTokenException extends RuntimeException {

    /**
     * Constructor QA-driven con mensaje automático.
     *
     * @param token El token inválido.
     */
    public InvalidTokenException(String token) {
        super("El token proporcionado no es válido: " + token);
    }
}
