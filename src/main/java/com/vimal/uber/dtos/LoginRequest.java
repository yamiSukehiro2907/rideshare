package com.vimal.uber.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username is required")
        @NotNull(message = "Username cannot be null")
        String username,

        @NotBlank(message = "Password is required")
        @NotNull(message = "Password cannot be null")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {
}

