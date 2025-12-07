package com.vimal.uber.controllers;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.models.User;
import com.vimal.uber.security.CustomUserDetails;
import com.vimal.uber.services.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DriverController {

    private final RideService rideService;

    @GetMapping("/driver/rides/requests")
    public ResponseEntity<ApiResponse<?>> getRequestedRides() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return new ResponseEntity<>(ApiResponse.error("Authentication Failed"), HttpStatus.UNAUTHORIZED);
        }
        User user = customUserDetails.getUser();
        if (!Objects.equals(user.getRole(), "DRIVER_ROLE")) {
            return new ResponseEntity<>(ApiResponse.error("Only drivers are allowed to accept rides!"), HttpStatus.UNAUTHORIZED);
        }
        return rideService.getRequestedRides();
    }

    @PostMapping("/driver/rides/{rideId}/accept")
    public ResponseEntity<ApiResponse<?>> acceptRide(@PathVariable("rideId") String rideId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return new ResponseEntity<>(ApiResponse.error("Authentication Failed"), HttpStatus.UNAUTHORIZED);
        }
        User user = customUserDetails.getUser();
        if (!Objects.equals(user.getRole(), "DRIVER_ROLE")) {
            return new ResponseEntity<>(ApiResponse.error("Only drivers are allowed to accept rides!"), HttpStatus.UNAUTHORIZED);
        }
        return rideService.acceptRide(rideId, customUserDetails.getId());
    }

    @PostMapping("/driver/rides/{rideId}/complete")
    public ResponseEntity<ApiResponse<?>> completeRide(@PathVariable("rideId") String rideId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return new ResponseEntity<>(ApiResponse.error("Authentication Failed"), HttpStatus.UNAUTHORIZED);
        }
        User user = customUserDetails.getUser();
        if (!Objects.equals(user.getRole(), "DRIVER_ROLE")) {
            return new ResponseEntity<>(ApiResponse.error("Only drivers are allowed to complete rides!"), HttpStatus.UNAUTHORIZED);
        }
        return rideService.completeRide(rideId, customUserDetails.getId());
    }
}
