package com.pm.guessword.controller;


import com.pm.guessword.dto.securityDTO.AuthRequest;
import com.pm.guessword.dto.securityDTO.JwtAuthResponse;
import com.pm.guessword.dto.securityDTO.RefreshTokenRequest;
import com.pm.guessword.model.User;
import com.pm.guessword.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<User> signUp (@RequestBody AuthRequest authRequest){
        User user = authService.signUp(authRequest);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/signIn")
    public ResponseEntity<JwtAuthResponse> signIn (@RequestBody AuthRequest authRequest){
        JwtAuthResponse response =  authService.signIn(authRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refresh (@RequestBody RefreshTokenRequest refreshTokenRequest){
        JwtAuthResponse response =  authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(response);
    }
}
