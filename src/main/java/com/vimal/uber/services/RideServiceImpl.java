package com.vimal.uber.services;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.dtos.RideInfoRequest;
import com.vimal.uber.models.Ride;
import com.vimal.uber.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> getRides(RideInfoRequest rideInfoRequest, String userId) {
        List<Ride> rides = rideRepository.findAll(rideInfoRequest, userId);
        return ResponseEntity.ok(ApiResponse.success("Rides fetched successfully", rides));
    }
}
