package org.otropets.travelplanner.auth.controller;

import jakarta.validation.Valid;
import org.otropets.travelplanner.auth.service.UserService;
import org.otropets.travelplanner.auth.dto.AuthResponse;
import org.otropets.travelplanner.auth.dto.LoginRequest;
import org.otropets.travelplanner.auth.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request)
    {
        AuthResponse response = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/api/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        AuthResponse response = service.login(request);
        return ResponseEntity.ok(response);
    }

}
