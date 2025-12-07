package com.vimal.uber.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 3, message = "Password must be at least 3 characters long")
        String password,

        @NotBlank(message = "Role is required!")
        String role
) {
}
