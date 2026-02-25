package com.pm.guessword.mapper;

import com.pm.guessword.dto.UserRequest;
import com.pm.guessword.dto.UserResponse;
import com.pm.guessword.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user){
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        return response;
    }

    public User toEntity(UserRequest userRequest){
        User user = new User();
        user.setUsername(userRequest.getUsername());
        return user;
    }
}
