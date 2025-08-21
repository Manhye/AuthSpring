package com.example.authspring.controller;

import com.example.authspring.dto.response.UserResponse;
import com.example.authspring.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<UserResponse> grantAdmin(@PathVariable Long userId){
        return ResponseEntity.ok(userService.grantAdmin(userId));
    }
}
