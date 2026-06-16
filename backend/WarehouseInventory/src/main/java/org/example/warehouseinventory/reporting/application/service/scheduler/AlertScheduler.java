package org.example.warehouseinventory.reporting.application.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.inventory.application.service.LotService;
import org.example.warehouseinventory.reporting.domain.event.ExpiredLotEvent;
import org.example.warehouseinventory.reporting.domain.event.LowStockEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor

public class AlertScheduler {

    private final ProductService productService;
    private final LotService lotService;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedRate = 60_000)
    @Transactional(readOnly = true)
    public void checkLowStock() {

        log.info("Running low stock check...");

        productService.findProductsBelowMinStock().forEach(
                p ->
                        eventPublisher.publishEvent(
                                new LowStockEvent(
                                        p.getProductId(),
                                        p.getSku(),
                                        p.getName(),
                                        p.getCurrentStock(),
                                        p.getMinStockLevel()
                                )
                        )
        );
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional(readOnly = true)
    public void checkExpiredLots() {

        log.info("Running expired lots check...");

        lotService.findExpiredLotsWithStock().forEach(
                lot ->
                        eventPublisher.publishEvent(
                                new ExpiredLotEvent(
                                        lot.getId(),
                                        lot.getLotNumber(),
                                        lot.getProduct().getSku(),
                                        lot.getExpirationDate(),
                                        lot.getAvailableQuantity()
                                )
                        )
        );
    }
}