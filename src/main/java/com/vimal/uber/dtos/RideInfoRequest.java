package com.vimal.uber.dtos;

import jakarta.validation.constraints.NotNull;

public record RideInfoRequest(
        @NotNull
        String pickupLocation,
        @NotNull
        String dropLocation
) {
}
