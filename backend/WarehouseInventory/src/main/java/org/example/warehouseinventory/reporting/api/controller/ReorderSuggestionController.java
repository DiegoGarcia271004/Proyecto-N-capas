package org.example.warehouseinventory.reporting.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.reporting.application.service.ReorderSuggestionService;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reorder-suggestions")
@RequiredArgsConstructor
public class ReorderSuggestionController extends BaseController {

    private final ReorderSuggestionService reorderSuggestionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getAll() {

        return buildResponse(
                "Reorder suggestions retrieved successfully",
                HttpStatus.OK,
                reorderSuggestionService.getAll()
        );
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getPending() {

        return buildResponse(
                "Pending reorder suggestions retrieved successfully",
                HttpStatus.OK,
                reorderSuggestionService.getPending()
        );
    }

    @PatchMapping("/{id}/attended")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> markAsAttended(
            @PathVariable UUID id
    ) {

        return buildResponse(
                "Reorder suggestion marked as attended",
                HttpStatus.OK,
                reorderSuggestionService.markAsAttended(id)
        );
    }
}