package org.example.warehouseinventory.reporting.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.warehouseinventory.auth.application.service.UserDetailsServiceImpl;
import org.example.warehouseinventory.reporting.application.service.impl.AbcReportServiceImpl;
import org.example.warehouseinventory.reporting.domain.dto.response.AbcReportResponse;
import org.example.warehouseinventory.reporting.domain.enums.AbcCategory;
import org.example.warehouseinventory.shared.utils.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AbcReportController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class AbcReportControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean
    UserDetailsServiceImpl userDetailsService;
    @MockitoBean
    AbcReportServiceImpl abcReportService;

    // --- seguridad ---

    @Test
    void getAbcReport_returnsUnauthorized_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/reporting/abc")
                        .param("from", "2024-01-01")
                        .param("to",   "2024-12-31"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getAbcReport_returnsForbidden_whenOperatorRole() throws Exception {
        mockMvc.perform(get("/api/reporting/abc")
                        .param("from", "2024-01-01")
                        .param("to",   "2024-12-31"))
                .andExpect(status().isForbidden());
    }

    // --- happy path ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAbcReport_returnsOk_whenAdminRole() throws Exception {
        when(abcReportService.generateAbcReport(any(), any())).thenReturn(emptyReport());

        mockMvc.perform(get("/api/reporting/abc")
                        .param("from", "2024-01-01")
                        .param("to",   "2024-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void getAbcReport_returnsOk_whenWarehouseManagerRole() throws Exception {
        when(abcReportService.generateAbcReport(any(), any())).thenReturn(emptyReport());

        mockMvc.perform(get("/api/reporting/abc")
                        .param("from", "2024-01-01")
                        .param("to",   "2024-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAbcReport_returnsExpectedFields_inResponseBody() throws Exception {
        var report = new AbcReportResponse(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                new BigDecimal("2000.00"),
                Map.of(AbcCategory.A, 2L, AbcCategory.B, 1L, AbcCategory.C, 1L),
                List.of()
        );
        when(abcReportService.generateAbcReport(any(), any())).thenReturn(report);

        mockMvc.perform(get("/api/reporting/abc")
                        .param("from", "2024-01-01")
                        .param("to",   "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalConsumptionValue").value(2000.0))
                .andExpect(jsonPath("$.data.countByCategory.A").value(2))
                .andExpect(jsonPath("$.data.countByCategory.B").value(1))
                .andExpect(jsonPath("$.data.countByCategory.C").value(1));
    }

    // --- validación de parámetros ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAbcReport_returnsBadRequest_whenFromParamMissing() throws Exception {
        mockMvc.perform(get("/api/reporting/abc")
                        .param("to", "2024-12-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAbcReport_returnsBadRequest_whenToParamMissing() throws Exception {
        mockMvc.perform(get("/api/reporting/abc")
                        .param("from", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAbcReport_returnsBadRequest_whenDateFormatInvalid() throws Exception {
        mockMvc.perform(get("/api/reporting/abc")
                        .param("from", "01-01-2024")  // formato incorrecto
                        .param("to",   "2024-12-31"))
                .andExpect(status().isBadRequest());
    }

    // --- helper ---

    private AbcReportResponse emptyReport() {
        return new AbcReportResponse(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                BigDecimal.ZERO,
                Map.of(),
                List.of()
        );
    }
}