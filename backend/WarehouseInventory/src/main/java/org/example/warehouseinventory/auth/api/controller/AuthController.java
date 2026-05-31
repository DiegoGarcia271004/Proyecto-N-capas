package org.example.warehouseinventory.auth.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.auth.domain.dto.request.LoginRequest;
import org.example.warehouseinventory.auth.domain.dto.request.RegisterRequest;
import org.example.warehouseinventory.auth.domain.dto.response.TokenResponse;
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
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        User user = (User) authentication.getPrincipal();
        assert user != null;
        String token = jwtService.generateToken(user);

        return buildResponse("Login successfully", HttpStatus.OK, new TokenResponse(token));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new BusinessRuleViolationException("Username " + req.username() + " is already taken");
        }

        User user = User.builder()
                .username(req.username())
                .password(passwordEncoder.encode(req.password()))
                .role(req.role())
                .active(true)
                .build();

        userRepository.save(user);
        return buildResponse("User registered successfully", HttpStatus.CREATED, null);
    }
}
