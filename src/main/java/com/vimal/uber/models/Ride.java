package com.vimal.uber.models;

import com.vimal.uber.enums.RideStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "rides")
@Data
public class Ride {
    @Id
    private String rideId;

    private String userId;

    private String pickupLocation;

    private String dropLocation;

    private RideStatus rideStatus = RideStatus.REQUESTED;

    @CreatedDate
    private LocalDateTime createdAt;
}
