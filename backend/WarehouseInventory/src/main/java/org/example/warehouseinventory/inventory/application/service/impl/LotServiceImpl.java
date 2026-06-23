package org.example.warehouseinventory.inventory.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.inventory.application.service.LotService;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.infrastructure.repository.LotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Lot> findExpiredLotsWithStock() {

        return lotRepository.findExpiredLotsWithStock();
    }
}