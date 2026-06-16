package org.example.warehouseinventory.reporting.application.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.warehouseinventory.reporting.domain.entity.Notification;
import org.example.warehouseinventory.reporting.domain.event.ExpiredLotEvent;
import org.example.warehouseinventory.reporting.domain.event.LowStockEvent;
import org.example.warehouseinventory.reporting.repository.NotificationRepository;
import org.example.warehouseinventory.shared.domain.enums.NotificationType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor

public class AlertListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    @Transactional
    public void onLowStock(LowStockEvent event) {

        if (notificationRepository.existsByRelatedEntityIdAndTypeAndReadFalse(
                event.product(), NotificationType.LOW_STOCK
        )) return;

        String message = String.format(
                "LOW STOCK ALERT - Product [%s] %s: current stock %d is below minimum level %d.",
                event.sku(), event.name(),
                event.currentStock(), event.minStockLevel()
        );

        log.warn(message);

        Notification notification = Notification.create(
                NotificationType.LOW_STOCK,
                message,
                event.product(),
                "PRODUCT"
        );
    }

    @EventListener
    @Transactional
    public void onExpiredLot(ExpiredLotEvent event) {

        if (notificationRepository.existsByRelatedEntityIdAndTypeAndReadFalse(
                event.lot(), NotificationType.EXPIRED_LOT
        )) return;

        String message = String.format(
                "EXPIRED LOT ALERT - Lot [%s] of product [%s] expired on %s with %d units still available.",
                event.lotNumber(), event.productSku(),
                event.expirationDate(), event.availableQuantity()
        );

        log.warn(message);

        Notification notification = Notification.create(
                NotificationType.EXPIRED_LOT,
                message,
                event.lot(),
                "LOT"
        );

        notificationRepository.save(notification);
    }

}