package org.example.warehouseinventory.reporting.application.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.warehouseinventory.reporting.domain.entity.Notification;
import org.example.warehouseinventory.reporting.domain.event.ExpiredLotEvent;
import org.example.warehouseinventory.reporting.domain.event.LowStockEvent;
import org.example.warehouseinventory.reporting.domain.event.ResolveExpiredLotEvent;
import org.example.warehouseinventory.reporting.domain.event.ResolveLowStockEvent;
import org.example.warehouseinventory.reporting.repository.NotificationRepository;
import org.example.warehouseinventory.shared.domain.enums.NotificationType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor

public class AlertListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onLowStock(LowStockEvent event) {

        String message = String.format(
                "LOW STOCK ALERT - Product [%s] %s: current stock %d is below minimum level %d.",
                event.sku(), event.name(),
                event.currentStock(), event.minStockLevel()
        );

        verifyIfNeedUpdateOrCreate(
                event.product(),
                NotificationType.LOW_STOCK,
                message,
                "PRODUCT",
                event.currentStock()
        );
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onExpiredLot(ExpiredLotEvent event) {

        String message = String.format(
                "EXPIRED LOT ALERT - Lot [%s] of product [%s] expired on %s with %d units still available.",
                event.lotNumber(), event.productSku(),
                event.expirationDate(), event.availableQuantity()
        );

        verifyIfNeedUpdateOrCreate(
                event.lot(),
                NotificationType.EXPIRED_LOT,
                message,
                "LOT",
                event.availableQuantity()
        );
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onResolveLowStock(ResolveLowStockEvent event) {

        notificationRepository.findByTypeAndResolvedFalse(NotificationType.LOW_STOCK)
                .forEach(notification -> {

                    if (!event.activeProductIds().contains(notification.getRelatedEntityId())) {

                        notification.resolve();
                        notificationRepository.save(notification);
                        log.info("LOW STOCK RESOLVED — Product {}",
                                notification.getRelatedEntityId());
                    }
                });
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onResolveExpiredLot(ResolveExpiredLotEvent event) {

        notificationRepository.findByTypeAndResolvedFalse(NotificationType.EXPIRED_LOT)
                .forEach(notification -> {

                    if (!event.activeLotsIds().contains(notification.getRelatedEntityId())) {

                        notification.resolve();
                        notificationRepository.save(notification);
                        log.info("EXPIRED LOT RESOLVED — Lot {}",
                                notification.getRelatedEntityId());
                    }
                });
    }

    private void verifyIfNeedUpdateOrCreate(UUID id, NotificationType type, String message, String relatedEntityType, Integer current) {

        Optional<Notification> existing = notificationRepository.findByRelatedEntityIdAndType(
                id, type
        );

        if (existing.isPresent()) {

            if (!existing.get().getCurrentStock().equals(current)) {

                existing.get().updateStockAlert(message, current);
                notificationRepository.save(existing.get());

                log.warn("UPDATED - {}", message);
            }

            return;
        }

        log.warn(message);

        Notification notification = Notification.create(
                type,
                message,
                id,
                relatedEntityType,
                current
        );

        notificationRepository.save(notification);
    }

}