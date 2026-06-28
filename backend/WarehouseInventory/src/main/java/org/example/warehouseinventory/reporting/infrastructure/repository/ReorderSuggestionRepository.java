package org.example.warehouseinventory.reporting.infrastructure.repository;

import org.example.warehouseinventory.reporting.domain.entity.ReorderSuggestion;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReorderSuggestionRepository extends JpaRepository<ReorderSuggestion, UUID> {

    Optional<ReorderSuggestion> findByProductAndWarehouseAndAttendedFalse(
            UUID product, UUID warehouse
    );

    List<ReorderSuggestion> findByAttendedFalse();
    @NullMarked
    List<ReorderSuggestion> findAll();
}