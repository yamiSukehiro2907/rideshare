package com.vimal.uber.dtos;

import jakarta.validation.constraints.NotBlank;

public record RideInfoRequest(
        @NotBlank(message = "Pickup is required")
        String pickupLocation,
        @NotBlank(message = "Drop is required")
        String dropLocation
) {
}
