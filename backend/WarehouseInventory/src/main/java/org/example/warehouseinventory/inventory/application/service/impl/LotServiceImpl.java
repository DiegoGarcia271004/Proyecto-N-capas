package org.example.warehouseinventory.inventory.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.domain.dto.response.ReorderProjection;
import org.example.warehouseinventory.inventory.application.service.LotService;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.infrastructure.repository.LotRepository;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Lot> findExpiredLotsWithStock() {

        return lotRepository.findExpiredLotsWithStock();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReorderProjection> findProductsBelowReorderPoint() {

        return lotRepository.findProductsBelowReorderPoint();
    }

    @Override
    @Transactional(readOnly = true)
    public Lot findById(UUID id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lot not found with id: " + id));
    }

    @Override
    @Transactional
    public void applyDiscrepancy(Lot lot, Integer discrepancy) {
        lot.applyDiscrepancy(discrepancy);
        lotRepository.save(lot);
    }
}