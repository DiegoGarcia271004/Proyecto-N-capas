package org.example.warehouseinventory.reporting.repository;

import org.example.warehouseinventory.reporting.domain.entity.Notification;
import org.example.warehouseinventory.shared.domain.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByReadFalseAndResolvedFalse();
    List<Notification> findByResolvedFalse();
    List<Notification> findByTypeAndResolvedFalse(NotificationType type);
    Optional<Notification> findByRelatedEntityIdAndType(UUID relatedEntityId, NotificationType type);
}