package org.example.warehouseinventory.auth.application.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.auth.api.mapper.UserMapper;
import org.example.warehouseinventory.auth.domain.dto.request.LoginRequest;
import org.example.warehouseinventory.auth.domain.dto.request.RegisterRequest;
import org.example.warehouseinventory.auth.domain.dto.response.UserResponse;
import org.example.warehouseinventory.auth.domain.entity.User;
import org.example.warehouseinventory.auth.infraestructure.repository.UserRepository;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.utils.config.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public void login(LoginRequest req, HttpServletResponse res) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        User user = (User) auth.getPrincipal();
        String token = jwtService.generateToken(user);

        res.addCookie(jwtService.createTokenCookie(token));
    }

    public void logout(HttpServletResponse res) {
        res.addCookie(jwtService.clearTokenCookie());
    }

    public UserResponse register(RegisterRequest req) {
        if(userRepository.existsByUsername(req.username())) {
            throw new BusinessRuleViolationException("Username " + req.username() + " is already taken");
        }

        User user = User.builder()
                .username(req.username())
                .password(passwordEncoder.encode(req.password()))
                .role(req.role())
                .build();

        return userMapper.toDto(userRepository.save(user));
    }
}
