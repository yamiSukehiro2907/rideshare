package com.vimal.uber.helpers;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.dtos.RideDto;
import com.vimal.uber.enums.RideStatus;
import com.vimal.uber.models.Ride;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RideHelper {

    public static RideDto getRideDto(Ride ride) {
        RideDto rideDto = new RideDto();
        rideDto.setRideId(ride.getRideId());
        rideDto.setPickupLocation(ride.getPickupLocation());
        rideDto.setDropLocation(ride.getDropLocation());
        rideDto.setUserId(ride.getUserId());
        rideDto.setRideStatus(ride.getRideStatus());
        rideDto.setCreatedDate(ride.getCreatedAt());
        return rideDto;
    }

    public static ResponseEntity<ApiResponse<?>> validateRideForCompletion(Ride ride) {
        if (ride == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Ride not found!"));
        }
        if (ride.getRideStatus() == RideStatus.COMPLETED) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Ride already completed!"));
        }
        if (ride.getRideStatus() == RideStatus.REQUESTED) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Ride not accepted yet!"));
        }
        return null;
    }

    public static ResponseEntity<ApiResponse<?>> validateRideForAcceptance(Ride ride) {
        if (ride == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Ride not found!"));
        }
        if (ride.getRideStatus() == RideStatus.COMPLETED) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Ride already completed!"));
        }
        if (ride.getRideStatus() == RideStatus.ACCEPTED) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Ride already accepted!"));
        }
        return null;
    }

}
