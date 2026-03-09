package com.pm.guessword.service;

import com.pm.guessword.dto.UserRequest;
import com.pm.guessword.dto.UserResponse;
import com.pm.guessword.enums.Role;
import com.pm.guessword.exception.UserNotFoundException;
import com.pm.guessword.mapper.UserMapper;
import com.pm.guessword.model.User;
import com.pm.guessword.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User user = userMapper.toEntity(request);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var saved = userRepository.save(user);

        log.info("Created new User '{}'", saved.getUsername());

        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!user.getUsername().equals(userRequest.getUsername()) && userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        user.setUsername(userRequest.getUsername());
        if (userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        var updated = userRepository.save(user);

        log.info("Updated user id {}: username '{}'", userId, user.getUsername());

        return userMapper.toResponse(updated);

    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        log.info("Retrieved user id {}, username '{}'", userId, user.getUsername());

        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);

        log.info("Deleted user id {}", userId);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String username, int size, String sortBy, String direction, int page, Role roleFilter) {

        Sort.Direction dir = Sort.Direction.fromString(direction);

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));

        Page<User> users;

        if ((username != null && !username.isBlank()) && roleFilter != null) {
            users = userRepository.findByUsernameContainingIgnoreCaseAndRole(username, roleFilter, pageable);
        } else if (username != null && !username.isBlank()) {
            users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        } else if (roleFilter != null) {
            users = userRepository.findByRole(roleFilter, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        log.info("Requested users page {}, size {}, sorted by {} {}, username filter '{}', role filter '{}', returned {} records",
                page, size, sortBy, direction, username, roleFilter, users.getNumberOfElements());

        return users.map(userMapper::toResponse);
    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authentication required");
        }

        return (User) authentication.getPrincipal();
    }



}
