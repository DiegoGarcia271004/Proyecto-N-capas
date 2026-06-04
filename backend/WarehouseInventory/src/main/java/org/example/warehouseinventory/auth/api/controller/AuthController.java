package org.example.warehouseinventory.auth.api.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.auth.application.service.AuthService;
import org.example.warehouseinventory.auth.domain.dto.request.LoginRequest;
import org.example.warehouseinventory.auth.domain.dto.request.RegisterRequest;
import org.example.warehouseinventory.auth.domain.dto.response.CsrfTokenResponse;
import org.example.warehouseinventory.auth.domain.entity.User;
import org.example.warehouseinventory.auth.infraestructure.repository.UserRepository;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.example.warehouseinventory.shared.utils.config.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(@Valid @RequestBody LoginRequest req, HttpServletResponse res) {
        String csrfToken = authService.login(req, res);
        return buildResponse("Login successful", HttpStatus.OK, new CsrfTokenResponse(csrfToken));
    }


    @PostMapping("/logout")
    public ResponseEntity<GeneralResponse> logout(HttpServletResponse res) {
        authService.logout(res);
        return buildResponse("Logout successful", HttpStatus.OK, null);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse> register(@Valid @RequestBody RegisterRequest req) {
        return buildResponse("User registered successfully", HttpStatus.CREATED, authService.register(req));
    }
}
