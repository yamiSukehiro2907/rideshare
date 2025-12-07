package com.vimal.uber.dtos;

import com.vimal.uber.enums.RideStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RideDto {
    private String rideId;
    private String userId;
    private LocalDateTime createdDate;
    private RideStatus rideStatus;
    private String dropLocation;
    private String pickupLocation;
}
