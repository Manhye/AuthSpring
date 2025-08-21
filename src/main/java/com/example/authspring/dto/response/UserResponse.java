package com.example.authspring.dto.response;

import com.example.authspring.domain.Role;

import java.util.Set;

public record UserResponse(
        String username,
        String nickname,
        Set<Role> roles
) {
}
