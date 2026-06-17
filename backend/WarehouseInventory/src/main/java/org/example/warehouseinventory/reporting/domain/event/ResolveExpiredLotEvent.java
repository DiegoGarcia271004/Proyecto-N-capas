package org.example.warehouseinventory.reporting.domain.event;

import java.util.Set;
import java.util.UUID;

public record ResolveExpiredLotEvent(
        Set<UUID> activeLotsIds
) { }