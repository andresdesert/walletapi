package com.cyberwallet.walletapi.exception;

/**
 * Excepción que se lanza cuando no se encuentra un usuario.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje detallado.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
