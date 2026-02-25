package com.pm.guessword.service;

import com.pm.guessword.dto.UserRequest;
import com.pm.guessword.dto.UserResponse;
import com.pm.guessword.enums.Role;
import com.pm.guessword.mapper.UserMapper;
import com.pm.guessword.model.User;
import com.pm.guessword.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User user = userMapper.toEntity(request);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " not found"));
        if (!user.getUsername().equals(userRequest.getUsername()) && userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        user.setUsername(userRequest.getUsername());
        if(userRequest.getPassword() != null && !userRequest.getPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        var updated = userRepository.save(user);
        return userMapper.toResponse(updated);

    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(user -> userMapper.toResponse(user));
    }
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id " + userId + " does not exist");
        }
        userRepository.deleteById(userId);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authentication required");
        }

        return (User) authentication.getPrincipal();
    }

}
