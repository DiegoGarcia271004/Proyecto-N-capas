package org.example.warehouseinventory.auth.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.auth.application.service.AuthService;
import org.example.warehouseinventory.auth.domain.dto.request.LoginRequest;
import org.example.warehouseinventory.auth.domain.dto.request.RegisterRequest;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {
    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(@Valid @RequestBody LoginRequest req, HttpServletRequest request, HttpServletResponse res) {
        var userResponse = authService.login(req, res);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csrfToken, request, res);
        return buildResponse("Login successful", HttpStatus.OK, userResponse);
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
