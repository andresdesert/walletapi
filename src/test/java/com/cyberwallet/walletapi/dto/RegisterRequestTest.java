package com.cyberwallet.walletapi.dto;

import com.cyberwallet.walletapi.dto.auth.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("🟢 Registro válido pasa validación")
    void validRequestShouldPassValidation() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Juan")
                .lastname("Perez")
                .email("juan.perez@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("🔴 Registro con campos vacíos genera errores")
    void emptyFieldsShouldFailValidation() {
        RegisterRequest request = new RegisterRequest();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).hasSizeGreaterThanOrEqualTo(3); // firstname, email, password
    }

    @Test
    @DisplayName("🔴 Email inválido debe fallar")
    void invalidEmailShouldFail() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Nombre")
                .lastname("Apellido")
                .email("email-invalido")
                .password("password123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().toLowerCase().contains("inválido"));

    }
}
