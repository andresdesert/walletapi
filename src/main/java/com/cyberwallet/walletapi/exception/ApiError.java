package com.cyberwallet.walletapi.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private int statusCode;         // Código HTTP (ej: 404)
    private String error;           // Texto del error (ej: "Not Found")
    private String message;         // Detalle técnico o de usuario
    private String path;            // Ruta donde ocurrió
    private LocalDateTime timestamp; // Momento del error
}
