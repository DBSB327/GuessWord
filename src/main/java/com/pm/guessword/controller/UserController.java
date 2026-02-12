package com.pm.guessword.controller;

import com.pm.guessword.dto.UserRequest;
import com.pm.guessword.dto.UserResponse;
import com.pm.guessword.model.User;
import com.pm.guessword.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userResponses = userService.getAllUsers();
        return ResponseEntity.ok(userResponses);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.updateUser(userId, userRequest);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
