package com.pm.guessword.dto;

import com.pm.guessword.enums.Role;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private Role role;
}
