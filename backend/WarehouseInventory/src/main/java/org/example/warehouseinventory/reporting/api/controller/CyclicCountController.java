package org.example.warehouseinventory.reporting.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.reporting.application.service.CyclicCountService;
import org.example.warehouseinventory.reporting.domain.dto.request.CreateCyclicCountRequest;
import org.example.warehouseinventory.reporting.domain.dto.request.SubmitPhysicalCountRequest;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.example.warehouseinventory.shared.domain.enums.CountStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cyclic-counts")
@RequiredArgsConstructor
public class CyclicCountController extends BaseController {

    private final CyclicCountService cyclicCountService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> create(
            @Valid @RequestBody CreateCyclicCountRequest request
    ) {

        return buildResponse(
                "Cyclic count created successfully",
                HttpStatus.CREATED,
                cyclicCountService.create(request)
        );
    }

    @PatchMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'OPERATOR')")
    public ResponseEntity<GeneralResponse> start(
            @PathVariable UUID id
    ) {

        return buildResponse(
                "Cyclic count started",
                HttpStatus.OK,
                cyclicCountService.start(id)
        );
    }

    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'OPERATOR')")
    public ResponseEntity<GeneralResponse> submit(
            @PathVariable UUID id,
            @Valid @RequestBody SubmitPhysicalCountRequest request
    ) {

        return buildResponse(
                "Cyclic count submitted and adjusted",
                HttpStatus.OK,
                cyclicCountService.submitCount(id, request)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getAll() {

        return buildResponse(
                "Cyclic counts retrieved successfully",
                HttpStatus.OK,
                cyclicCountService.getAll()
        );
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getByStatus(
            @PathVariable CountStatus status
    ) {

        return buildResponse(
                "Cyclic counts retrieved successfully",
                HttpStatus.OK,
                cyclicCountService.getByStatus(status)
        );
    }
}