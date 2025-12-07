package com.vimal.uber.repositories;

import com.vimal.uber.dtos.RideInfoRequest;
import com.vimal.uber.models.Ride;

import java.util.List;

public interface RideRepository {

    List<Ride> findAll(RideInfoRequest rideInfoRequest, String userId);
}
