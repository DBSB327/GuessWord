package com.pm.guessword.security;

import com.pm.guessword.dto.securityDTO.AuthRequest;
import com.pm.guessword.dto.securityDTO.JwtAuthResponse;
import com.pm.guessword.dto.securityDTO.RefreshTokenRequest;
import com.pm.guessword.enums.Role;
import com.pm.guessword.model.User;
import com.pm.guessword.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User signUp(AuthRequest authRequest) {
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public JwtAuthResponse signIn(AuthRequest authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        var user = userRepository.findByUsernameIgnoreCase(authRequest.getUsername()).orElseThrow(() -> new IllegalStateException("Username not found"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setToken(jwt);
        jwtAuthResponse.setRefreshToken(refreshToken);

        return jwtAuthResponse;
    }

    public JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String username = jwtService.extractUserName(refreshTokenRequest.getToken());
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new IllegalStateException("Username not found"));

        if(jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            var jwt = jwtService.generateToken(user);

            JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();

            jwtAuthResponse.setToken(jwt);
            jwtAuthResponse.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthResponse;
        }
        return null;
    }
}
