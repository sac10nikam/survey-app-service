package com.marketlogic.survey.controller;

import com.marketlogic.survey.model.*;
import com.marketlogic.survey.service.SurveyService;
import com.marketlogic.survey.util.SurveyConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/survey")
@Slf4j
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @Operation(summary = "Get all surveys")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey details are retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PagedResponse.class))})})
    @GetMapping("/all")
    public ResponseEntity<PagedResponse> getAllSurveys(@RequestParam(value = "page", defaultValue = SurveyConstants.DEFAULT_PAGE_NUMBER) int page,
                                                       @RequestParam(value = "size", defaultValue = SurveyConstants.DEFAULT_PAGE_SIZE) int size) {
        log.debug("SurveyController.getAllSurveys");
        return new ResponseEntity<>(surveyService.getAllSurveys(page, size), OK);
    }

    @Operation(summary = "Get all survey questions")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Questions are retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))})})
    @GetMapping("/questions")
    public ResponseEntity<List<String>> getAllSurveysQuestions() {
        log.debug("SurveyController.getAllSurveysQuestions");
        return new ResponseEntity<>(surveyService.getAllSurveysQuestions(), OK);
    }

    @Operation(summary = "Get survey question details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Question details are retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))})})
    @GetMapping("/{surveyId}/details")
    public ResponseEntity<QuestionResponse> getQuestionDetails(@PathVariable Long surveyId) {
        log.debug("SurveyController.getQuestionDetails");
        return new ResponseEntity<>(surveyService.getQuestionDetails(surveyId), OK);
    }

    @Operation(summary = "Add survey details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Survey details are added successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))})})
    @PostMapping("/add")
    public ResponseEntity<?> addSurvey(@RequestParam Long userId, @Valid @RequestBody SurveyRequest surveyRequest) {
        log.debug("SurveyController.addSurvey");
        SurveyView surveyView = surveyService.addSurvey(userId, surveyRequest);

        ApiResponse apiResponse = null;
        apiResponse = ApiResponse.builder()
                .success(true)
                .message("Survey created successfully!: " + surveyView.getId())
                .build();
        return new ResponseEntity<>(apiResponse, CREATED);
    }

    @Operation(summary = "Update survey details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey details are updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))})})
    @PutMapping("/{surveyId}")
    public ResponseEntity<?> updateSurvey(@RequestParam Long userId, @PathVariable Long surveyId, @Valid @RequestBody SurveyRequest surveyRequest) {
        log.debug("SurveyController.updateSurvey");
        SurveyView surveyView = surveyService.updateSurvey(userId, surveyId, surveyRequest);

        ApiResponse apiResponse = null;
        apiResponse = ApiResponse.builder()
                .success(true)
                .message("Survey updated successfully!: " + surveyView.getId())
                .build();
        return new ResponseEntity<>(apiResponse, OK);
    }

    @Operation(summary = "Delete survey details by id")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey details are deleted successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))})})
    @DeleteMapping("/{surveyId}")
    public ResponseEntity<?> deleteSurvey(@RequestParam Long userId, @PathVariable Long surveyId) {
        log.debug("SurveyController.deleteSurvey");

        boolean isDeleted = surveyService.deleteSurvey(userId, surveyId);

        ApiResponse apiResponse = null;
        apiResponse = ApiResponse.builder()
                .success(isDeleted)
                //.message(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(surveyView))
                .message(isDeleted == true ? "Survey details are deleted successfully!" : "Survey not deleted")
                .build();
        return new ResponseEntity<>(apiResponse, OK);
    }

    @Operation(summary = "Get survey details by id")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Survey details are retrieved for given id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = SurveyView.class))})})
    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyView> getSurveyDetails(@PathVariable Long surveyId) {
        log.debug("SurveyController.getSurveyDetails");
        return new ResponseEntity<>(surveyService.getSurveyDetailsById(surveyId), OK);
    }

    @Operation(summary = "Cast answers to a survey")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Survey answers are added successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = SurveyView.class))})})
    @PostMapping("/{surveyId}/votes")
    public ResponseEntity<SurveyView> castAnswers(@RequestParam Long userId,
                                                  @PathVariable Long surveyId,
                                                  @Valid @RequestBody VoteRequest voteRequest) {
        log.debug("SurveyController.castAnswers");
        surveyService.castAnswersAndGetUpdatedSurvey(userId, surveyId, voteRequest);
        return new ResponseEntity("You have successfully responded to a survey!", CREATED);
    }

}
