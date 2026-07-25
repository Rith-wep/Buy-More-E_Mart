package com.buymore.backend.controller;

import com.buymore.backend.dto.AdminCreateUserRequest;
import com.buymore.backend.dto.UserResponse;
import com.buymore.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody AdminCreateUserRequest request) {
        UserResponse response = userService.createByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
