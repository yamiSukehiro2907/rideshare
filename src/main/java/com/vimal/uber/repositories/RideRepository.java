package com.vimal.uber.repositories;

import com.vimal.uber.models.Ride;

import java.util.List;

public interface RideRepository {

    Ride save(Ride ride);

    List<Ride> findAll();
}
