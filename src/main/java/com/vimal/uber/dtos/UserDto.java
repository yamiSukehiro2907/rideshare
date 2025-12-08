package com.vimal.uber.dtos;

import java.time.LocalDateTime;

public record UserDto(
        String userId,
        String username,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
