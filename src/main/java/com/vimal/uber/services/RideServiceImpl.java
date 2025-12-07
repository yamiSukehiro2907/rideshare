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

    @Override
    public ResponseEntity<ApiResponse<?>> acceptRide(String rideId) {
        Ride ride = rideRepository.findById(rideId);
        if (ride == null) {
            return new ResponseEntity<>(ApiResponse.error("Ride not found!"), HttpStatus.NOT_FOUND);
        }
        if (ride.getRideStatus().equals(RideStatus.COMPLETED)) {
            return new ResponseEntity<>(ApiResponse.error("Ride already completed!"), HttpStatus.BAD_REQUEST);
        }
        if (ride.getRideStatus().equals(RideStatus.ACCEPTED)) {
            return new ResponseEntity<>(ApiResponse.error("Ride already accepted!"), HttpStatus.BAD_REQUEST);
        }
        ride.setRideStatus(RideStatus.ACCEPTED);
        Ride updatedRide = rideRepository.update(ride);
        RideDto rideDto = RideHelper.getRideDto(updatedRide);
        return ResponseEntity.ok(ApiResponse.success("Ride accepted successfully!", rideDto));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId);
        if (ride == null) {
            return new ResponseEntity<>(ApiResponse.error("Ride not found!"), HttpStatus.NOT_FOUND);
        }
        if (ride.getRideStatus().equals(RideStatus.COMPLETED)) {
            return new ResponseEntity<>(ApiResponse.error("Ride already completed!"), HttpStatus.BAD_REQUEST);
        }
        if (ride.getRideStatus().equals(RideStatus.REQUESTED)) {
            return new ResponseEntity<>(ApiResponse.error("Ride not accepted yet!"), HttpStatus.BAD_REQUEST);
        }
        ride.setRideStatus(RideStatus.COMPLETED);
        Ride updatedRide = rideRepository.update(ride);
        RideDto rideDto = RideHelper.getRideDto(updatedRide);
        return ResponseEntity.ok(ApiResponse.success("Ride completed successfully!", rideDto));
    }

}
