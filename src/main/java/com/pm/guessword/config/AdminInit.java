package com.pm.guessword.config;

import com.pm.guessword.enums.Role;
import com.pm.guessword.model.User;
import com.pm.guessword.repository.UserRepository;
import com.pm.guessword.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInit implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        String adminUsername = "admin";
        if(userRepository.findByUsernameIgnoreCase(adminUsername).isEmpty()){
            user.setUsername(adminUsername);
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRole(Role.ADMIN);
            userRepository.save(user);
        }
        else{
            System.out.println("Admin already exists");
        }

    }



}
