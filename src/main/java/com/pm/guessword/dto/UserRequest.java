package com.pm.guessword.dto;

import com.pm.guessword.enums.Role;
import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;

}
