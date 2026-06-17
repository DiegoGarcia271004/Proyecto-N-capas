package org.example.warehouseinventory.reporting.api.mapper;

import org.example.warehouseinventory.reporting.domain.dto.response.NotificationResponse;
import org.example.warehouseinventory.reporting.domain.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationMapper {

    public NotificationResponse toDto(Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getMessage(),
                notification.getRelatedEntityId(),
                notification.getRelatedEntityType(),
                notification.getRead(),
                notification.getResolved(),
                notification.getCreatedAt()
        );
    }

    public List<NotificationResponse> toDtoList(List<Notification> notifications) {

        return notifications.stream()
                .map(this::toDto).toList();
    }

}