package com.cyberwallet.walletapi.service;

import com.cyberwallet.walletapi.dto.auth.AuthenticationRequest;
import com.cyberwallet.walletapi.dto.auth.AuthenticationResponse;
import com.cyberwallet.walletapi.dto.auth.RegisterRequest;
import com.cyberwallet.walletapi.entity.User;
import com.cyberwallet.walletapi.enums.Role;
import com.cyberwallet.walletapi.exception.EmailAlreadyUsedException;
import com.cyberwallet.walletapi.exception.InvalidCredentialsException;
import com.cyberwallet.walletapi.exception.UserNotFoundException;
import com.cyberwallet.walletapi.repository.UserRepository;
import com.cyberwallet.walletapi.security.JwtService;
import com.cyberwallet.walletapi.service.impl.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("🟢 Registro exitoso devuelve token JWT")
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("secure123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedpassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthenticationResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("🔴 Registro falla por email ya registrado")
    void shouldThrowEmailAlreadyUsedException() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Existing")
                .lastname("User")
                .email("used@example.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("🟢 Login exitoso devuelve JWT")
    void shouldAuthenticateSuccessfully() {
        AuthenticationRequest request = new AuthenticationRequest("user@email.com", "password");

        User user = User.builder()
                .email(request.getEmail())
                .password("encoded")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthenticationResponse response = authService.authenticate(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("🔴 Login con credenciales inválidas lanza excepción custom con mensaje claro")
    void shouldThrowInvalidCredentialsException() {
        AuthenticationRequest request = new AuthenticationRequest("fail@email.com", "wrongpass");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any());

        InvalidCredentialsException ex = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.authenticate(request)
        );

        assertThat(ex.getMessage()).contains("Credenciales inválidas");
    }


    @Test
    @DisplayName("🔴 Login con usuario inexistente lanza excepción custom")
    void shouldThrowUserNotFoundException() {
        AuthenticationRequest request = new AuthenticationRequest("nouser@email.com", "somepass");

        // Simulamos que pasa la autenticación (no lanza BadCredentialsException)
        when(authenticationManager.authenticate(any())).thenReturn(mock(org.springframework.security.core.Authentication.class));

        // Simulamos que el usuario no existe en la base
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Verificamos que se lanza la excepción personalizada
        assertThrows(UserNotFoundException.class, () -> authService.authenticate(request));
    }
}
