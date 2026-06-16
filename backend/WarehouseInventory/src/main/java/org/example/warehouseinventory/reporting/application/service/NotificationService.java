package org.example.warehouseinventory.reporting.application.service;

import org.example.warehouseinventory.reporting.domain.dto.response.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    List<NotificationResponse> getAll();
    List<NotificationResponse> getUnread();
    NotificationResponse markAsRead(UUID id);

}