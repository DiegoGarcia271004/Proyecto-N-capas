package org.example.warehouseinventory.reporting.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.reporting.api.mapper.NotificationMapper;
import org.example.warehouseinventory.reporting.application.service.NotificationService;
import org.example.warehouseinventory.reporting.domain.dto.response.NotificationResponse;
import org.example.warehouseinventory.reporting.domain.entity.Notification;
import org.example.warehouseinventory.reporting.repository.NotificationRepository;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAll() {

        return notificationMapper.toDtoList(notificationRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnread() {

        return notificationMapper.toDtoList(notificationRepository.findByReadFalse());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(UUID id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with ID: " + id
                ));

        notification.markAsRead();
        return notificationMapper.toDto(notificationRepository.save(notification));
    }
}