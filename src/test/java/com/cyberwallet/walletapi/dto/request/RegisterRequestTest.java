package com.cyberwallet.walletapi.dto.request;

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
    @DisplayName("🔴 firstname vacío debe fallar")
    void shouldFailWhenFirstnameIsBlank() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("") // <== valor vacío para forzar la falla
                .lastname("Apellido")
                .email("test@domain.com")
                .password("secure123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("firstname") &&
                        v.getMessage().toLowerCase().contains("obligatorio"));
    }
    @Test
    @DisplayName("🔴 firstname nulo debe fallar")
    void shouldFailWhenFirstnameIsNull() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname(null) // <== valor nulo
                .lastname("Apellido")
                .email("valid@email.com")
                .password("secure123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("firstname") &&
                        v.getMessage().toLowerCase().contains("obligatorio"));
    }

    @Test
    @DisplayName("🔴 lastname vacío debe fallar")
    void shouldFailWhenLastnameIsBlank() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Nombre")
                .lastname("") // <== valor vacío para forzar la falla
                .email("test@domain.com")
                .password("secure123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("lastname") &&
                        v.getMessage().toLowerCase().contains("obligatorio"));
    }

    @Test
    @DisplayName("🔴 lastname nulo debe fallar")
    void shouldFailWhenLastnameIsNull() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Nombre")
                .lastname(null) // <== valor nulo para forzar la falla
                .email("test@domain.com")
                .password("secure123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("lastname") &&
                        v.getMessage().toLowerCase().contains("obligatorio"));
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
    @Test
    @DisplayName("🔴 email nulo debe fallar")
    void shouldFailWhenEmailIsNull() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Nombre")
                .lastname("Apellido")
                .email(null) // <== valor nulo
                .password("secure123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().toLowerCase().contains("obligatorio"));
    }

    @Test
    @DisplayName("🔴 email vacío debe fallar")
    void shouldFailWhenEmailIsBlank() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Nombre")
                .lastname("Apellido")
                .email("") // <== valor vacío para forzar la falla
                .password("secure123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().toLowerCase().contains("obligatorio"));
    }


    @Test
    @DisplayName("🔴 password nulo debe fallar")
    void shouldFailWhenPasswordIsNull() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Nombre")
                .lastname("Apellido")
                .email("valid@email.com")
                .password(null) // <== valor nulo
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("password") &&
                        v.getMessage().toLowerCase().contains("la contraseña es obligatoria"));
    }

    @Test
    @DisplayName("🔴 password vacío debe fallar")
    void shouldFailWhenPasswordIsBlank() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Nombre")
                .lastname("Apellido")
                .email("valid@email.com")
                .password("") // <== valor vacío
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("password") &&
                        v.getMessage().toLowerCase().contains("obligatoria"));
    }

    @Test
    @DisplayName("🔴 firstname vacío + email inválido deben devolver errores combinados")
    void shouldFailWhenFirstnameBlankAndEmailInvalid() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("") // error
                .lastname("Apellido")
                .email("email-invalido") // error
                .password("secure123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("firstname") &&
                        v.getMessage().toLowerCase().contains("obligatorio"));
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().toLowerCase().contains("inválido"));
    }

    @Test
    @DisplayName("🔴 lastname nulo + email inválido deben devolver errores combinados")
    void shouldFailWhenLastnameNullAndEmailInvalid() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Nombre")
                .lastname(null) // error
                .email("email-invalido") // error
                .password("secure123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("lastname") &&
                        v.getMessage().toLowerCase().contains("obligatorio"));
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().toLowerCase().contains("inválido"));
    }

    @Test
    @DisplayName("🔴 password nulo + email inválido deben devolver errores combinados")
    void shouldFailWhenPasswordNullAndEmailInvalid() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Nombre")
                .lastname("Apellido")
                .email("email-invalido") // error
                .password(null) // error
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("password") &&
                        v.getMessage().toLowerCase().contains("obligatoria"));
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().toLowerCase().contains("inválido"));
    }




}
