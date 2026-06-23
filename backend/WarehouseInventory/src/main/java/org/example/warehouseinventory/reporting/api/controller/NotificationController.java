package org.example.warehouseinventory.reporting.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.reporting.application.service.NotificationService;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor

public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getAll() {

        return buildResponse(
                "Notifications retrieved successfully.",
                HttpStatus.OK,
                notificationService.getAll()
        );
    }

    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getUnread() {

        return buildResponse(
                "Unread notifications retrieved successfully.",
                HttpStatus.OK,
                notificationService.getUnread()
        );
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> markAsRead(
            @PathVariable UUID id
    ) {

        return buildResponse(
                "Notification marked as read.",
                HttpStatus.OK,
                notificationService.markAsRead(id)
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getActive() {

        return buildResponse(
                "Active notifications retrieved successfully.",
                HttpStatus.OK,
                notificationService.getActive()
        );
    }

}