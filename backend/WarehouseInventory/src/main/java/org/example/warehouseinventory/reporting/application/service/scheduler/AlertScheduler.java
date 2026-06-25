package org.example.warehouseinventory.reporting.application.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.catalog.domain.dto.response.LowStockProjection;
import org.example.warehouseinventory.catalog.domain.dto.response.ReorderProjection;
import org.example.warehouseinventory.inventory.application.service.LotService;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.reporting.application.service.ReorderSuggestionService;
import org.example.warehouseinventory.reporting.domain.event.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor

public class AlertScheduler {

    private final ProductService productService;
    private final LotService lotService;
    private final ApplicationEventPublisher eventPublisher;
    private final ReorderSuggestionService reorderSuggestionService;

    @Scheduled(fixedRate = 10_000)
    @Transactional(readOnly = true) 
    public void checkLowStock() {

        log.info("Running low stock check...");

        List<LowStockProjection> belowMinStock = productService.findProductsBelowMinStock();

        if (!belowMinStock.isEmpty()) {

            Set<UUID> belowMinStockIds = belowMinStock.stream()
                    .map(LowStockProjection::getProductId)
                    .collect(Collectors.toSet());

            belowMinStock.forEach(p ->
                    eventPublisher.publishEvent(new LowStockEvent(
                            p.getProductId(), p.getSku(), p.getName(),
                            p.getCurrentStock(), p.getMinStockLevel()
                    ))
            );

            eventPublisher.publishEvent(new ResolveLowStockEvent(belowMinStockIds));
        }
    }

    @Scheduled(fixedRate = 10_000)
    @Transactional(readOnly = true)
    public void checkExpiredLots() {

        log.info("Running expired lots check...");

        List<Lot> expiredLots = lotService.findExpiredLotsWithStock();

        if (!expiredLots.isEmpty()) {

            Set<UUID> expiredLotsIds = expiredLots.stream()
                    .map(Lot::getId)
                    .collect(Collectors.toSet());

            expiredLots.forEach(l ->
                    eventPublisher.publishEvent(new ExpiredLotEvent(
                            l.getId(), l.getLotNumber(),
                            l.getProduct().getSku(),
                            l.getExpirationDate(),
                            l.getAvailableQuantity()
                    ))
            );

            eventPublisher.publishEvent(new ResolveExpiredLotEvent(expiredLotsIds));
        }
    }

    @Scheduled(fixedRate = 10_000)
    @Transactional(readOnly = true)
    public void checkReorderPoints() {

        log.info("Running reorder point check...");

        List<ReorderProjection> productsBelowReorderPoint = lotService.findProductsBelowReorderPoint();

        if (!productsBelowReorderPoint.isEmpty()) {

            productsBelowReorderPoint.forEach(p -> {

                ReorderSuggestionEvent event = new ReorderSuggestionEvent(
                        p.getProductId(),
                        p.getWarehouseId(),
                        p.getSku(),
                        p.getCurrentStock(),
                        p.getReorderPoint(),
                        0,
                        0.0
                );

                reorderSuggestionService.processReorderSuggestion(event);
            });
        }
    }
}