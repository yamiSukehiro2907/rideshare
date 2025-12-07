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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RideController {

    private final RideService rideService;

    @PostMapping("/rides")
    public ResponseEntity<ApiResponse<?>> getRides(@Valid @RequestBody RideInfoRequest rideInfoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return new ResponseEntity<>(ApiResponse.error("Authentication Failed"), HttpStatus.UNAUTHORIZED);
        }
        RideInfoRequest cleanedRideInfoRequest = new RideInfoRequest(
                rideInfoRequest.pickupLocation().trim(),
                rideInfoRequest.dropLocation().trim()
        );
        return rideService.getRides(cleanedRideInfoRequest, customUserDetails.getId());
    }

}
