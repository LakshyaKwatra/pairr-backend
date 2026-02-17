package com.connect.pairr.auth;

import com.connect.pairr.model.dto.AuthResponse;
import com.connect.pairr.model.dto.LoginRequest;
import com.connect.pairr.model.dto.RegisterRequest;
import com.connect.pairr.model.entity.User;
import com.connect.pairr.model.enums.Role;
import com.connect.pairr.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // --- Register ---

    @Test
    void register_happyPath() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(new RegisterRequest("new@test.com", "pass123", "New User"));

        assertEquals("jwt-token", response.token());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("new@test.com", saved.getEmail());
        assertEquals("New User", saved.getDisplayName());
        assertEquals("encoded_pass", saved.getPassword());
        assertEquals(Role.USER, saved.getRole());
    }

    @Test
    void register_duplicateEmail_throws() {
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);
        assertThrows(RuntimeException.class,
                () -> authService.register(new RegisterRequest("existing@test.com", "pass", "User")));
        verify(userRepository, never()).save(any());
    }

    // --- Login ---

    @Test
    void login_happyPath() {
        User user = User.builder().id(UUID.randomUUID()).email("user@test.com")
                .password("encoded").displayName("User").role(Role.USER).build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "encoded")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(new LoginRequest("user@test.com", "pass123"));
        assertEquals("jwt-token", response.token());
    }

    @Test
    void login_emailNotFound_throws() {
        when(userRepository.findByEmail("nobody@test.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> authService.login(new LoginRequest("nobody@test.com", "pass")));
    }

    @Test
    void login_wrongPassword_throws() {
        User user = User.builder().id(UUID.randomUUID()).email("user@test.com")
                .password("encoded").displayName("User").role(Role.USER).build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> authService.login(new LoginRequest("user@test.com", "wrong")));
    }
}
