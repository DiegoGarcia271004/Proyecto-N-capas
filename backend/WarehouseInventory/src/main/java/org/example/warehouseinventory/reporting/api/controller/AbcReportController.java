package org.example.warehouseinventory.reporting.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.reporting.application.service.AbcReportService;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reporting/abc")
@RequiredArgsConstructor
public class AbcReportController extends BaseController {

    private final AbcReportService abcReportService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getAbcReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
            ){
        return buildResponse("ABC report generated successfully",
                HttpStatus.OK,
                abcReportService.generateAbcReport(from, to));
    }

}
