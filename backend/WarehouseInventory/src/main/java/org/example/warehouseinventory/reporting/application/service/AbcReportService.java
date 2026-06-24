package org.example.warehouseinventory.reporting.application.service;

import org.example.warehouseinventory.reporting.domain.dto.response.AbcReportResponse;

import java.time.LocalDate;

public interface AbcReportService {
    AbcReportResponse generateAbcReport(LocalDate from, LocalDate to);
}
