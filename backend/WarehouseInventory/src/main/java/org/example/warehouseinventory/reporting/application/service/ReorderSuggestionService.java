package org.example.warehouseinventory.reporting.application.service;

import org.example.warehouseinventory.reporting.domain.dto.response.ReorderSuggestionResponse;
import org.example.warehouseinventory.reporting.domain.event.ReorderSuggestionEvent;

import java.util.List;
import java.util.UUID;

public interface ReorderSuggestionService {

    List<ReorderSuggestionResponse> getAll();
    List<ReorderSuggestionResponse> getPending();
    ReorderSuggestionResponse markAsAttended(UUID id);
    void processReorderSuggestion(ReorderSuggestionEvent event);
}