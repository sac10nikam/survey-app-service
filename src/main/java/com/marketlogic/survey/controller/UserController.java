package com.marketlogic.survey.controller;

import com.marketlogic.survey.model.PagedResponse;
import com.marketlogic.survey.model.UserIdentityAvailability;
import com.marketlogic.survey.model.UserView;
import com.marketlogic.survey.service.SurveyService;
import com.marketlogic.survey.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.marketlogic.survey.util.SurveyConstants.DEFAULT_PAGE_NUMBER;
import static com.marketlogic.survey.util.SurveyConstants.DEFAULT_PAGE_SIZE;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {

    private final SurveyService surveyService;
    private final UserService userService;

    public UserController(SurveyService surveyService, UserService userService) {
        this.surveyService = surveyService;
        this.userService = userService;
    }

    @Operation(summary = "Checks user availability")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Checks user availability", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserIdentityAvailability.class))})})
    @GetMapping("/user/check-username-availability")
    public ResponseEntity<UserIdentityAvailability> checkUsernameAvailability(@RequestParam(value = "username") String username) {
        log.debug("UserController.checkUsernameAvailability");

        Boolean isAvailable = !userService.existsByUsername(username);
        return new ResponseEntity(UserIdentityAvailability.builder()
                .available(isAvailable)
                .build(), OK);
    }

    @Operation(summary = "Checks email availability")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Checks email availability", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserIdentityAvailability.class))})})
    @GetMapping("/user/check-email-availability")
    public ResponseEntity<UserIdentityAvailability> checkEmailAvailability(@RequestParam(value = "email") String email) {
        log.debug("UserController.checkEmailAvailability");

        Boolean isAvailable = userService.existsByEmail(email);
        return new ResponseEntity(UserIdentityAvailability.builder()
                .available(isAvailable)
                .build(), OK);
    }

    @Operation(summary = "Retrieves user details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User details retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserIdentityAvailability.class))})})
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserView> getUserSurveyDetails(@PathVariable(value = "userId") Long userId) {
        log.debug("UserController.getUserSurveyDetails");
        UserView userView = userService.getUserSurveyDetails(userId);
        return new ResponseEntity(userView, OK);
    }

    @Operation(summary = "Retrieves user details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User survey details retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserIdentityAvailability.class))})})
    @GetMapping("/users/{userId}/survey-list")
    public ResponseEntity<PagedResponse> getUserSurveys(@PathVariable(value = "userId") Long userId,
                                                        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                        @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        log.debug("UserController.getUserSurveys");
        return new ResponseEntity(surveyService.getUserSurveys(userId, page, size), OK);
    }

    @Operation(summary = "Retrieves user survey answer details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User survey answer details retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserIdentityAvailability.class))})})
    @GetMapping("/users/{userId}/votes")
    public ResponseEntity<PagedResponse> getUserSurveyAnswers(@PathVariable(value = "userId") Long userId,
                                                              @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                              @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        log.debug("UserController.getUserSurveyAnswers");
        return new ResponseEntity(surveyService.getUserSurveyAnswers(userId, page, size), OK);
    }

}
