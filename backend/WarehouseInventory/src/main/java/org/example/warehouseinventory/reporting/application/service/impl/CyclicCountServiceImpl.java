package org.example.warehouseinventory.reporting.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.inventory.application.service.LotService;
import org.example.warehouseinventory.inventory.application.service.StockMovementService;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.reporting.api.mapper.CyclicCountMapper;
import org.example.warehouseinventory.reporting.application.service.CyclicCountService;
import org.example.warehouseinventory.reporting.domain.dto.request.CreateCyclicCountRequest;
import org.example.warehouseinventory.reporting.domain.dto.request.SubmitPhysicalCountRequest;
import org.example.warehouseinventory.reporting.domain.dto.response.CyclicCountResponse;
import org.example.warehouseinventory.reporting.domain.entity.CyclicCount;
import org.example.warehouseinventory.reporting.infrastructure.repository.CyclicCountRepository;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.shared.domain.enums.CountStatus;
import org.example.warehouseinventory.shared.domain.enums.MovementType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CyclicCountServiceImpl implements CyclicCountService {

    private final CyclicCountRepository cyclicCountRepository;
    private final LotService lotService;
    private final StockMovementService stockMovementService;
    private final CyclicCountMapper cyclicCountMapper;

    @Override
    @Transactional
    public CyclicCountResponse create(CreateCyclicCountRequest request) {

        Lot lot = lotService.findById(request.lot());

        cyclicCountRepository.findByLotAndStatusNot(request.lot(), CountStatus.ADJUSTED)
                .ifPresent(existing -> {
                    throw new BusinessRuleViolationException("An active cyclic count already exists for lot: " + request.lot());
                });

        CyclicCount cc = CyclicCount.create(request.lot(), lot.getAvailableQuantity());
        return cyclicCountMapper.toDto(cyclicCountRepository.save(cc));
    }

    @Override
    @Transactional
    public CyclicCountResponse start(UUID id) {

        CyclicCount cc = findById(id);
        cc.start();

        return cyclicCountMapper.toDto(cyclicCountRepository.save(cc));
    }

    @Override
    @Transactional
    public CyclicCountResponse submitCount(UUID id, SubmitPhysicalCountRequest request) {

        CyclicCount cc = findById(id);
        cc.adjust(request.physicalQuantity());

        if (cc.getDiscrepancy() != 0) {

            Lot lot = lotService.findById(cc.getLot());

            stockMovementService.recordAdjustment(lot, cc.getDiscrepancy());
            lotService.applyDiscrepancy(lot, cc.getDiscrepancy());
        }

        return cyclicCountMapper.toDto(cyclicCountRepository.save(cc));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CyclicCountResponse> getByStatus(CountStatus status) {
        return cyclicCountMapper.toDtoList(cyclicCountRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CyclicCountResponse> getAll() {
        return cyclicCountMapper.toDtoList(cyclicCountRepository.findAll());
    }

    private CyclicCount findById(UUID id) {

        return cyclicCountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cyclic count not found with id: " + id));
    }
}