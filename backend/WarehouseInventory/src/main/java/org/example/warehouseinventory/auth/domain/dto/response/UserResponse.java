package org.example.warehouseinventory.auth.domain.dto.response;

import org.example.warehouseinventory.auth.domain.entity.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        Role role,
        Boolean active
) {
}
