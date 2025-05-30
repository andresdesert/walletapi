package com.cyberwallet.walletapi.security;

import com.cyberwallet.walletapi.entity.User;
import com.cyberwallet.walletapi.enums.Role;
import com.cyberwallet.walletapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    @DisplayName("🟢 Carga exitosa de usuario por email")
    void shouldLoadUserByEmailSuccessfully() {
        User user = User.builder()
                .email("test@domain.com")
                .password("secure123")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("test@domain.com")).thenReturn(Optional.of(user));

        var userDetails = userDetailsService.loadUserByUsername("test@domain.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@domain.com");
    }

    @Test
    @DisplayName("🔴 Lanza excepción cuando el usuario no existe")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("notfound@domain.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("notfound@domain.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("notfound@domain.com");
    }
}
