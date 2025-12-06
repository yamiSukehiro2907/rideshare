package com.vimal.uber.controllers;

import com.vimal.uber.dtos.ApiResponse;
import com.vimal.uber.dtos.LoginRequest;
import com.vimal.uber.dtos.SignUpRequest;
import com.vimal.uber.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        SignUpRequest cleanedRequest = new SignUpRequest(signUpRequest.username(), signUpRequest.password().trim());

        if (cleanedRequest.password().length() < 6) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Password should be at least 6 characters long"));
        }
        return authService.createUser(cleanedRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        return authService.loginUser(auth, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logOut(HttpServletRequest request, HttpServletResponse response) {
        return authService.logOutUser(request, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshUser(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshUser(request, response);
    }
}
