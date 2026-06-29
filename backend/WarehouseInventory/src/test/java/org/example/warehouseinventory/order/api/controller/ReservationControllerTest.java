package org.example.warehouseinventory.order.api.controller;

import org.example.warehouseinventory.auth.application.service.UserDetailsServiceImpl;
import org.example.warehouseinventory.inventory.domain.exception.InsufficientStockException;
import org.example.warehouseinventory.order.application.service.ReservationService;
import org.example.warehouseinventory.order.domain.dto.request.ReservationRequest;
import org.example.warehouseinventory.order.domain.dto.response.ReservationResponse;
import org.example.warehouseinventory.order.domain.enums.ReservationStatus;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.shared.utils.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.MediaType.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

@WebMvcTest(ReservationController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
class ReservationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean UserDetailsServiceImpl userDetailsService;
    @MockitoBean
    ReservationService reservationService;

    // ── POST /api/reservation ────────────────────────────────────────

    @Test
    void createReservation_noToken_returns401() throws Exception {
        mockMvc.perform(post("/api/reservation")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void createReservation_validRequest_returns201() throws Exception {
        ReservationResponse response = buildResponse();
        when(reservationService.createReservation(any())).thenReturn(response);

        mockMvc.perform(post("/api/reservation")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Reservation created successfully"))
                .andExpect(jsonPath("$.data.quantity").value(30));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void createReservation_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/api/reservation")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void createReservation_insufficientStock_returns422() throws Exception {
        when(reservationService.createReservation(any()))
                .thenThrow(new InsufficientStockException("product-id", 100, 30));

        mockMvc.perform(post("/api/reservation")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isUnprocessableContent());
    }

    // ── PUT /api/reservation/confirm/{id} ─────────────────────────────

    @Test
    @WithMockUser(roles = "OPERATOR")
    void confirmReservation_validId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(reservationService.confirmReservation(id)).thenReturn(buildResponse());

        mockMvc.perform(put("/api/reservation/confirm/{id}", id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reservation confirmed successfully"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void confirmReservation_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(reservationService.confirmReservation(id))
                .thenThrow(new ResourceNotFoundException("Reservation not found with id: " + id));

        mockMvc.perform(put("/api/reservation/confirm/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void confirmReservation_notActive_returns422() throws Exception {
        UUID id = UUID.randomUUID();
        when(reservationService.confirmReservation(id))
                .thenThrow(new BusinessRuleViolationException("Only ACTIVE reservations can be confirmed"));

        mockMvc.perform(put("/api/reservation/confirm/{id}", id)
                        .with(csrf()))
                .andExpect(status().isUnprocessableContent());
    }

    // ── PUT /api/reservation/release/{id} ─────────────────────────────

    @Test
    @WithMockUser(roles = "OPERATOR")
    void releaseReservation_validId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(reservationService.releaseReservation(id)).thenReturn(buildResponse());

        mockMvc.perform(put("/api/reservation/release/{id}", id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reservation released successfully"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void releaseReservation_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(reservationService.releaseReservation(id))
                .thenThrow(new ResourceNotFoundException("Reservation not found with id: " + id));

        mockMvc.perform(put("/api/reservation/release/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // ── Fixtures ───────────────────────────────────────────────────

    private ReservationRequest buildRequest() {
        return new ReservationRequest(UUID.randomUUID(), UUID.randomUUID(), 30, 15);
    }

    private ReservationResponse buildResponse() {
        return new ReservationResponse(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "SKU-001",
                30, ReservationStatus.ACTIVE, LocalDateTime.now().plusMinutes(15)
        );
    }
}