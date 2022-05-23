package com.marketlogic.survey.service;

import com.marketlogic.survey.data.*;
import com.marketlogic.survey.exception.BadRequestException;
import com.marketlogic.survey.exception.ResourceNotFoundException;
import com.marketlogic.survey.model.LoginRequest;
import com.marketlogic.survey.model.SignUpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Collections;

@Service
@Slf4j
public class LoginService {

    @Autowired
    private UserRepository userRepository;
    private final RoleRepository roleRepository;

    public LoginService(RoleRepository roleRepository) {
        //this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User createUser(SignUpRequest signUpRequest) {
        log.debug("UserService.createUser");

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email Address already in use!");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("User Role not set."));

        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .username(signUpRequest.getUsername())
                .password(Base64.getEncoder().encodeToString(String.valueOf(signUpRequest.getPassword()).getBytes()))
                .roles(Collections.singleton(userRole))
                .createdAt(OffsetDateTime.now())
                .build();

        return userRepository.saveAndFlush(user);
    }

    public void validateUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid user!"));
        byte[] passwordBytes = Base64.getDecoder().decode(user.getPassword());
        if (!new String(passwordBytes).equals(loginRequest.getPassword())) {
            throw new BadRequestException("Invalid user!");
        }
    }
}
