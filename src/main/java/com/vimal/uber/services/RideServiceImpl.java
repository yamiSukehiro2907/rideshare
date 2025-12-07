package com.vimal.uber.services;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.dtos.RideDto;
import com.vimal.uber.dtos.RideInfoRequest;
import com.vimal.uber.enums.RideStatus;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Ride created successfully!", rideDto));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getRequestedRides() {
        List<RideDto> ridesDto = rideRepository.findAll().stream().map(RideHelper::getRideDto).toList();
        return ResponseEntity.ok(ApiResponse.success("Rides found!", ridesDto));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> acceptRide(String rideId) {
        Ride ride = rideRepository.findById(rideId);
        ResponseEntity<ApiResponse<?>> validationError = RideHelper.validateRideForAcceptance(ride);
        if (validationError != null) {
            return validationError;
        }
        ride.setRideStatus(RideStatus.ACCEPTED);
        Ride updatedRide = rideRepository.update(ride);
        RideDto rideDto = RideHelper.getRideDto(updatedRide);
        return ResponseEntity.ok(ApiResponse.success("Ride accepted successfully!", rideDto));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId);
        ResponseEntity<ApiResponse<?>> validationError = RideHelper.validateRideForCompletion(ride);
        if (validationError != null) {
            return validationError;
        }
        ride.setRideStatus(RideStatus.COMPLETED);
        Ride updatedRide = rideRepository.update(ride);
        RideDto rideDto = RideHelper.getRideDto(updatedRide);
        return ResponseEntity.ok(ApiResponse.success("Ride completed successfully!", rideDto));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getMyRides(String userId) {
        List<RideDto> ridesDto = rideRepository.findByUserId(userId).stream().map(RideHelper::getRideDto).toList();
        return ResponseEntity.ok(ApiResponse.success("Rides found!", ridesDto));
    }
}