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
public class PassengerController {
    private final RideService rideService;

    @GetMapping("/user/rides")
    public ResponseEntity<ApiResponse<?>> getRides() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return new ResponseEntity<>(ApiResponse.error("Authentication Failed!"), HttpStatus.UNAUTHORIZED);
        }
        if (!customUserDetails.getUser().getRole().equals("USER_ROLE")) {
            return new ResponseEntity<>(ApiResponse.error("Only Passengers allowed"), HttpStatus.UNAUTHORIZED);
        }
        return rideService.getMyRides(customUserDetails.getId());
    }

    @PostMapping("/rides")
    public ResponseEntity<ApiResponse<?>> createRide(@Valid @RequestBody RideInfoRequest rideInfoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return new ResponseEntity<>(ApiResponse.error("Authentication Failed"), HttpStatus.UNAUTHORIZED);
        }
        if (!customUserDetails.getUser().getRole().equals("USER_ROLE")) {
            return new ResponseEntity<>(ApiResponse.error("Only Passengers allowed"), HttpStatus.UNAUTHORIZED);
        }
        RideInfoRequest cleanedRideInfoRequest = new RideInfoRequest(rideInfoRequest.pickupLocation().trim(), rideInfoRequest.dropLocation().trim());
        return rideService.createRide(cleanedRideInfoRequest, customUserDetails.getId());
    }
}
