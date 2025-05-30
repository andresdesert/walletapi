package com.cyberwallet.walletapi.exception;

/**
 * Excepción que se lanza cuando se intenta registrar un email ya existente.
 */
public class EmailAlreadyUsedException extends RuntimeException {

    /**
     * Constructor QA-driven para mensaje automático.
     *
     * @param email El email que ya está registrado.
     */
    public EmailAlreadyUsedException(String email) {
        super("El email ya está registrado: " + email);
    }
}
