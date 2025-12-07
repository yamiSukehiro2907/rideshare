package com.vimal.uber.helpers;

import com.vimal.uber.dtos.RideDto;
import com.vimal.uber.models.Ride;

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

}
