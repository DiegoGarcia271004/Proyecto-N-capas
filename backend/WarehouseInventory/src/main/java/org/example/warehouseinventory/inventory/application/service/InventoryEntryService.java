package org.example.warehouseinventory.inventory.application.service;

import org.example.warehouseinventory.inventory.domain.dto.request.InventoryEntryRequest;
import org.example.warehouseinventory.inventory.domain.dto.response.LotResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface InventoryEntryService {

    LotResponse registerEntry(InventoryEntryRequest request);
}