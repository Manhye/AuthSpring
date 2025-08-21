package com.example.authspring.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String nickname
) {
}
