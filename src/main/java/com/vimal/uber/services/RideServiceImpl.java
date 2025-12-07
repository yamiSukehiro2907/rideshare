package com.vimal.uber.services;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.dtos.RideDto;
import com.vimal.uber.dtos.RideInfoRequest;
import com.vimal.uber.helpers.RideHelper;
import com.vimal.uber.models.Ride;
import com.vimal.uber.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> createRide(RideInfoRequest rideInfoRequest, String userId) {
        Ride ride = new Ride();
        ride.setPickupLocation(rideInfoRequest.pickupLocation());
        ride.setDropLocation(rideInfoRequest.dropLocation());
        ride.setUserId(userId);
        Ride createdRide = rideRepository.save(ride);
        RideDto rideDto = RideHelper.getRideDto(createdRide);
        return new ResponseEntity<>(ApiResponse.success("Ride created successfully!", rideDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getRequestedRides() {
        List<Ride> rides = rideRepository.findAll();
        List<RideDto> ridesDto = rides.stream()
                .map(RideHelper::getRideDto)
                .toList();
        return new ResponseEntity<>(ApiResponse.success("Rides found!", ridesDto), HttpStatus.OK);
    }

}
