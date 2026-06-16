package org.example.warehouseinventory.reporting.domain.dto.response;

import lombok.Builder;
import org.example.warehouseinventory.shared.domain.enums.NotificationType;

import java.util.UUID;

@Builder
public record NotificationResponse(
        UUID id,
        NotificationType notification,
        String message,
        UUID relatedEntityId,
        String relatedEntityType,
        Boolean read,
        java.time.Instant createdAt
) { }

