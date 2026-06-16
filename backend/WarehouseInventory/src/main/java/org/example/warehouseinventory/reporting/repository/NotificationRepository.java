package org.example.warehouseinventory.reporting.repository;

import org.example.warehouseinventory.reporting.domain.entity.Notification;
import org.example.warehouseinventory.shared.domain.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByReadFalse();
    boolean existsByRelatedEntityIdAndTypeAndReadFalse(UUID relatedEntityId, NotificationType type);
}