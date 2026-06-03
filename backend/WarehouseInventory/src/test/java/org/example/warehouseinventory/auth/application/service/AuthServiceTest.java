package org.example.warehouseinventory.auth.application.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.warehouseinventory.auth.api.mapper.UserMapper;
import org.example.warehouseinventory.auth.domain.dto.request.LoginRequest;
import org.example.warehouseinventory.auth.domain.dto.request.RegisterRequest;
import org.example.warehouseinventory.auth.domain.dto.response.UserResponse;
import org.example.warehouseinventory.auth.domain.entity.Role;
import org.example.warehouseinventory.auth.domain.entity.User;
import org.example.warehouseinventory.auth.infraestructure.repository.UserRepository;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.utils.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    AuthService authService;


    // ── login ──────────────────────────────────────────────────────

    @Test
    void login_validCredentials_setsCookieAndReturnsCsrfToken() {
        LoginRequest req = new LoginRequest("admin", "password123");
        User user = buildUser();
        Authentication auth = mock(Authentication.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Cookie cookie = new Cookie("access_token", "token");

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.createTokenCookie("jwt-token")).thenReturn(cookie);

        String csrfToken = authService.login(req, response);

        assertThat(csrfToken).isNotNull();
        verify(response).addCookie(cookie);
        verify(jwtService).generateToken(user);
    }

    @Test
    void login_invalidCredentials_throwsAuthenticationException() {
        LoginRequest req = new LoginRequest("admin", "wrong");
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(req, response))
                .isInstanceOf(BadCredentialsException.class);

        verify(response, never()).addCookie(any());
    }

    // ── logout ─────────────────────────────────────────────────────

    @Test
    void logout_clearsCookie() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        Cookie clearedCookie = new Cookie("access_token", "");

        when(jwtService.clearTokenCookie()).thenReturn(clearedCookie);

        authService.logout(response);

        verify(response).addCookie(clearedCookie);
        verify(jwtService).clearTokenCookie();
    }

    // ── register ───────────────────────────────────────────────────

    @Test
    void register_success_returnsUserResponse() {
        RegisterRequest req = new RegisterRequest("newuser", "password123", Role.OPERATOR);
        User user = buildUser();
        UserResponse expected = buildUserResponse();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponse result = authService.register(req);

        assertThat(result.username()).isEqualTo("admin");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsBusinessRuleViolation() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("admin", "password123", Role.OPERATOR)))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("admin");

        verify(userRepository, never()).save(any());
    }

    // ── Fixtures ───────────────────────────────────────────────────

    private User buildUser() {
        return User.builder().username("admin").password("encoded-password").role(Role.ADMIN).active(true).build();
    }

    private UserResponse buildUserResponse() {
        return new UserResponse(UUID.randomUUID(), "admin", Role.ADMIN, true);
    }
}

