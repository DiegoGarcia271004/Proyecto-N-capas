package org.example.warehouseinventory.reporting.application.service;

import org.example.warehouseinventory.reporting.domain.dto.request.CreateCyclicCountRequest;
import org.example.warehouseinventory.reporting.domain.dto.request.SubmitPhysicalCountRequest;
import org.example.warehouseinventory.reporting.domain.dto.response.CyclicCountResponse;
import org.example.warehouseinventory.shared.domain.enums.CountStatus;

import java.util.List;
import java.util.UUID;

public interface CyclicCountService {

    CyclicCountResponse create(CreateCyclicCountRequest request);
    CyclicCountResponse start(UUID id);
    CyclicCountResponse submitCount(UUID id, SubmitPhysicalCountRequest request);
    List<CyclicCountResponse> getByStatus(CountStatus status);
    List<CyclicCountResponse> getAll();
}