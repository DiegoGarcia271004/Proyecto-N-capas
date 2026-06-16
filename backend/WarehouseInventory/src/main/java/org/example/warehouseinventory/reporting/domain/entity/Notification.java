package org.example.warehouseinventory.reporting.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.warehouseinventory.shared.domain.AuditableEntity;
import org.example.warehouseinventory.shared.domain.enums.NotificationType;

import java.util.UUID;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Notification extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private UUID relatedEntityId;

    @Column(nullable = false)
    private String relatedEntityType;

    @Column(nullable = false)
    private Boolean read;

    public static Notification create(NotificationType type, String message, UUID relatedEntityId, String relatedEntityType) {

        Notification not = new Notification();

        not.type = type;
        not.message = message;
        not.relatedEntityId = relatedEntityId;
        not.relatedEntityType = relatedEntityType;
        not.read = false;

        return not;
    }

    public void markAsRead() {
        this.read = true;
    }

}