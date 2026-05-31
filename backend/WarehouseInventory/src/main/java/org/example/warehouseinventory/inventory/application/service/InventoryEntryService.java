package org.example.warehouseinventory.inventory.application.service;

import org.example.warehouseinventory.inventory.domain.entity.Lot;

import java.time.LocalDate;
import java.util.UUID;

public interface InventoryEntryService {

    Lot registerEntry(UUID product, UUID warehouse, String lotNumber, Integer quantity, LocalDate expirationDate);
}