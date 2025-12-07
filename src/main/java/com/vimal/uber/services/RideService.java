package com.vimal.uber.services;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.dtos.RideInfoRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public interface RideService {

    ResponseEntity<ApiResponse<?>> getRides(RideInfoRequest rideInfoRequest , String userId);
}
