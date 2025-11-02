package com.bankapplicationmicroservices.api_gateway.controller;

import com.bankapplicationmicroservices.api_gateway.dto.AuthRequest;
import com.bankapplicationmicroservices.api_gateway.dto.AuthResponse;
import com.bankapplicationmicroservices.api_gateway.dto.RegisterRequest;
import com.bankapplicationmicroservices.api_gateway.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final UserService users;

    public AuthController(UserService users) {
        this.users = users;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest req) {
        users.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        return users.login(req.getUsername(), req.getPassword());
    }
}
