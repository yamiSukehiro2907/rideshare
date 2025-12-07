package com.vimal.uber.services;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.dtos.LoginResponse;
import com.vimal.uber.dtos.SignUpRequest;
import com.vimal.uber.models.User;
import com.vimal.uber.repositories.UserRepository;
import com.vimal.uber.security.CustomUserDetails;
import com.vimal.uber.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<ApiResponse<?>> createUser(SignUpRequest signUpRequest) {
        try {
            if (userRepository.loadUserByUsername(signUpRequest.username()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Username already exists"));
            }

            User user = new User();
            user.setPassword(passwordEncoder.encode(signUpRequest.password()));
            user.setUsername(signUpRequest.username());
            user.setRole(signUpRequest.role());
            userRepository.createUser(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User created successfully", null));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
        }
    }


    @Override
    public ResponseEntity<ApiResponse<?>> loginUser(Authentication authentication, HttpServletResponse response) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            assert userDetails != null;
            User user = userDetails.getUser();

            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            LoginResponse loginResponse = new LoginResponse(user.getId(), user.getUsername(), accessToken, refreshToken);
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
        }
    }
}

