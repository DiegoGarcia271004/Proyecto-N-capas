package org.example.warehouseinventory.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.warehouseinventory.auth.domain.entity.Role;
import org.example.warehouseinventory.shared.domain.StrongPassword;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank @StrongPassword String password,
        @NotNull Role role
) {
}
