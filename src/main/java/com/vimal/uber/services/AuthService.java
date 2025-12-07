package com.vimal.uber.services;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.dtos.SignUpRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {
    ResponseEntity<ApiResponse<?>> createUser(SignUpRequest signUpRequest);

    ResponseEntity<ApiResponse<?>> loginUser(Authentication authentication, HttpServletResponse response);
}
