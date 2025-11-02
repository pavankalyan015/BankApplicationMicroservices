package com.bankapplicationmicroservices.api_gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    private Set<String> roles; // e.g. ["ADMIN","USER"]
}
