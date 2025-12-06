package com.vimal.uber.services;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.dtos.LoginResponse;
import com.vimal.uber.dtos.SignUpRequest;
import com.vimal.uber.entities.User;
import com.vimal.uber.helpers.AuthHelper;
import com.vimal.uber.repositories.UserRepository;
import com.vimal.uber.security.CustomUserDetails;
import com.vimal.uber.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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

            generateTokens(response, userDetails, user);

            LoginResponse loginResponse = new LoginResponse(user.getId(), user.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
        }
    }

    private void generateTokens(HttpServletResponse response, CustomUserDetails userDetails, User user) {
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        response.addCookie(AuthHelper.createAccessTokenCookie(accessToken, 3600));
        response.addCookie(AuthHelper.createRefreshTokenCookie(refreshToken, 7 * 24 * 3600));

        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> logOutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getCookies() != null) {
                String accessToken = AuthHelper.getAccessTokenFromHttpRequest(request);
                if (accessToken != null) {
                    String userId = jwtUtil.getIdAccessToken(accessToken);
                    User user = userRepository.loadUserById(userId);
                    if (user != null) {
                        user.setRefreshToken("");
                        userRepository.save(user);
                    }
                }

                String refreshToken = AuthHelper.getRefreshTokenFromHttpRequest(request);
                if (refreshToken != null) {
                    String userId = jwtUtil.getIdRefreshToken(refreshToken);
                    User user = userRepository.loadUserById(userId);
                    if (user != null) {
                        user.setRefreshToken("");
                        userRepository.save(user);
                    }
                }
            }

            response.addCookie(AuthHelper.createAccessTokenCookie(null, 0));
            response.addCookie(AuthHelper.createRefreshTokenCookie(null, 0));

            return ResponseEntity.ok(ApiResponse.success("Logout successful", null));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> refreshUser(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token not provided"));
        }

        try {
            String refreshToken = AuthHelper.getRefreshTokenFromHttpRequest(request);

            if (refreshToken == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Token not provided"));
            }

            if (!jwtUtil.isValidRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid token"));
            }

            String userId = jwtUtil.getIdRefreshToken(refreshToken);
            User user = userRepository.loadUserById(userId);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found"));
            }

            if (!refreshToken.equals(user.getRefreshToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid token"));
            }

            CustomUserDetails userDetails = new CustomUserDetails(user);

            generateTokens(response, userDetails, user);

            return ResponseEntity.ok(ApiResponse.success("User refreshed successfully", null));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Internal server error"));
        }
    }
}

