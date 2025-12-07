package com.vimal.uber.controllers;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.security.CustomUserDetails;
import com.vimal.uber.services.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final RideService rideService;

    @GetMapping("/rides")
    public ResponseEntity<ApiResponse<?>> getRides() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return new ResponseEntity<>(ApiResponse.error("Authentication Failed!"), HttpStatus.UNAUTHORIZED);
        }
        return rideService.getMyRides(customUserDetails.getId());
    }

}

