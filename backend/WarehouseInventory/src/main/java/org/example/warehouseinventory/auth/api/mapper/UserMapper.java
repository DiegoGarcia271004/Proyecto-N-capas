package org.example.warehouseinventory.auth.api.mapper;

import org.example.warehouseinventory.auth.domain.dto.response.UserResponse;
import org.example.warehouseinventory.auth.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toDto(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isEnabled()
        );
    }
}
