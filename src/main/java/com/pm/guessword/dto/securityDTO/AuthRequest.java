package com.pm.guessword.dto.securityDTO;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
