package com.pm.guessword.dto.securityDTO;

import lombok.Data;

@Data
public class JwtAuthResponse {
    private String token;
    private String refreshToken;
}
