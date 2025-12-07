package com.vimal.uber.controllers;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.dtos.RideInfoRequest;
import com.vimal.uber.security.CustomUserDetails;
import com.vimal.uber.services.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RideController {

    private final RideService rideService;

    @PostMapping("/rides")
    public ResponseEntity<ApiResponse<?>> createRide(@Valid @RequestBody RideInfoRequest rideInfoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return new ResponseEntity<>(ApiResponse.error("Authentication Failed"), HttpStatus.UNAUTHORIZED);
        }
        RideInfoRequest cleanedRideInfoRequest = new RideInfoRequest(rideInfoRequest.pickupLocation().trim(), rideInfoRequest.dropLocation().trim());
        return rideService.createRide(cleanedRideInfoRequest, customUserDetails.getId());
    }

    @GetMapping("/driver/rides/requests")
    public ResponseEntity<ApiResponse<?>> getRequestedRides() {
        return rideService.getRequestedRides();
    }

    @PostMapping("/driver/rides/{rideId}/accept")
    public ResponseEntity<ApiResponse<?>> acceptRide(@PathVariable("rideId") String rideId) {
        return rideService.acceptRide(rideId);
    }

    @PostMapping("/driver/rides/{rideId}/complete")
    public ResponseEntity<ApiResponse<?>> completeRide(@PathVariable("rideId") String rideId) {
        return rideService.completeRide(rideId);
    }

}
