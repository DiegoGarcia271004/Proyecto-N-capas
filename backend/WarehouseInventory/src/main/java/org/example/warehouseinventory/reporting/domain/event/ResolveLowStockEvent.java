package org.example.warehouseinventory.reporting.domain.event;

import java.util.Set;
import java.util.UUID;

public record ResolveLowStockEvent(
        Set<UUID> activeProductIds
) { }