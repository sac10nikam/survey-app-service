package com.marketlogic.survey.controller;

import com.marketlogic.survey.data.User;
import com.marketlogic.survey.exception.BadRequestException;
import com.marketlogic.survey.model.ApiResponse;
import com.marketlogic.survey.model.LoginRequest;
import com.marketlogic.survey.model.SignUpRequest;
import com.marketlogic.survey.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.GenericValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Operation(summary = "Signin user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))})})
    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.debug("LoginController.login");

        loginService.validateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User logged in successfully")
                .build());
    }

    @Operation(summary = "Signup user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User details are saved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))})})
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        log.debug("LoginController.registerUser");

        if (!GenericValidator.isEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Invalid email!");
        }
        User user = loginService.createUser(signUpRequest);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(true)
                .message("User registered successfully: Id = " + user.getId())
                .build();
        return new ResponseEntity<>(apiResponse, CREATED);
    }
}
