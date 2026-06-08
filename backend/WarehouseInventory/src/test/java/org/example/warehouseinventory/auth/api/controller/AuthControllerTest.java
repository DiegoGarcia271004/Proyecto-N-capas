package org.example.warehouseinventory.auth.api.controller;

import org.example.warehouseinventory.auth.application.service.AuthService;
import org.example.warehouseinventory.auth.application.service.UserDetailsServiceImpl;
import org.example.warehouseinventory.auth.domain.dto.request.LoginRequest;
import org.example.warehouseinventory.auth.domain.dto.request.RegisterRequest;
import org.example.warehouseinventory.auth.domain.dto.response.UserResponse;
import org.example.warehouseinventory.auth.domain.entity.Role;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.utils.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;
    @MockitoBean
    UserDetailsServiceImpl userDetailsService;

    // ── POST /api/auth/login ───────────────────────────────────────

    @Test
    void login_validCredentials_returns200WithCsrfToken() throws Exception {
        when(authService.login(any(), any())).thenReturn("csrf-token-value");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("admin", "password123"))))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.message").value("Login successful"))
                .andExpect((ResultMatcher) jsonPath("$.data.csrfToken").value("csrf-token-value"));
    }

    @Test
    void login_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect((ResultMatcher) jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authService.login(any(), any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("admin", "wrong"))))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/auth/logout ──────────────────────────────────────

    @Test
    void logout_returns200() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.message").value("Logout successful"));
    }

    // ── POST /api/auth/register ────────────────────────────────────

    @Test
    void register_noToken_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRegisterRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void register_operatorRole_returns403() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRegisterRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_validRequest_returns201() throws Exception {
        UserResponse userResponse = new UserResponse(
                UUID.randomUUID(), "newuser", Role.OPERATOR, true);
        when(authService.register(any())).thenReturn(userResponse);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRegisterRequest())))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.message").value("User registered successfully"))
                .andExpect((ResultMatcher) jsonPath("$.data.username").value("newuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_duplicateUsername_returns422() throws Exception {
        when(authService.register(any()))
                .thenThrow(new BusinessRuleViolationException("Username newuser is already taken"));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRegisterRequest())))
                .andExpect(status().isUnprocessableContent())
                .andExpect((ResultMatcher) jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect((ResultMatcher) jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    // ── Fixtures ───────────────────────────────────────────────────

    private RegisterRequest buildRegisterRequest() {
        return new RegisterRequest("newuser", "PhneumonoUltraMicroscopic24!", Role.OPERATOR);
    }
}