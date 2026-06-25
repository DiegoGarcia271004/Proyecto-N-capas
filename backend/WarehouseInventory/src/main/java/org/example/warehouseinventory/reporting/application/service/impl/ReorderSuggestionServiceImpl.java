package org.example.warehouseinventory.reporting.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.inventory.application.service.StockMovementService;
import org.example.warehouseinventory.reporting.api.mapper.ReorderSuggestionMapper;
import org.example.warehouseinventory.reporting.application.service.ReorderSuggestionService;
import org.example.warehouseinventory.reporting.domain.dto.response.ReorderSuggestionResponse;
import org.example.warehouseinventory.reporting.domain.entity.ReorderSuggestion;
import org.example.warehouseinventory.reporting.domain.event.ReorderSuggestionEvent;
import org.example.warehouseinventory.reporting.infrastructure.repository.ReorderSuggestionRepository;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReorderSuggestionServiceImpl implements ReorderSuggestionService {

    private final ReorderSuggestionRepository reorderSuggestionRepository;
    private final StockMovementService stockMovementService;
    private final ReorderSuggestionMapper reorderSuggestionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReorderSuggestionResponse> getAll() {

        return reorderSuggestionMapper.toDtoList(reorderSuggestionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReorderSuggestionResponse> getPending() {

        return reorderSuggestionMapper.toDtoList(
                reorderSuggestionRepository.findByAttendedFalse());
    }

    @Override
    @Transactional
    public ReorderSuggestionResponse markAsAttended(UUID id) {

        ReorderSuggestion rs = reorderSuggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reorder suggestion not found with id: " + id));

        rs.markAsAttended();
        return reorderSuggestionMapper.toDto(reorderSuggestionRepository.save(rs));
    }

    @Override
    @Transactional
    public void processReorderSuggestion(ReorderSuggestionEvent event) {

        Integer totalExit = stockMovementService.getTotalExit(event.product(), event.warehouse());

        double avgDailyConsumption = totalExit / 90.0;

        int suggestedQuantity = (int) Math.ceil(avgDailyConsumption * 30);

        Optional<ReorderSuggestion> existing = reorderSuggestionRepository
                .findByProductAndWarehouseAndAttendedFalse(
                        event.product(), event.warehouse());

        if (existing.isPresent()) {

            existing.get().update(event.currentStock(), suggestedQuantity, avgDailyConsumption);
            reorderSuggestionRepository.save(existing.get());

            return;
        }

        ReorderSuggestion rs = ReorderSuggestion.create(
                event.product(),
                event.warehouse(),
                event.productSku(),
                event.currentStock(),
                event.reorderPoint(),
                suggestedQuantity,
                avgDailyConsumption
        );

        reorderSuggestionRepository.save(rs);
    }
}