package org.example.warehouseinventory.reporting.infrastructure.repository;

import org.example.warehouseinventory.reporting.domain.entity.CyclicCount;
import org.example.warehouseinventory.shared.domain.enums.CountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CyclicCountRepository extends JpaRepository<CyclicCount, UUID> {

    List<CyclicCount> findByStatus(CountStatus status);
    Optional<CyclicCount> findByLotAndStatusNot(UUID lot, CountStatus status);
}