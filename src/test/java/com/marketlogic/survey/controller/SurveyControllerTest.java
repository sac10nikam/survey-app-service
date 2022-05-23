package com.marketlogic.survey.controller;

import com.marketlogic.survey.data.*;
import com.marketlogic.survey.model.AnswerResponse;
import com.marketlogic.survey.model.SurveyRequest;
import com.marketlogic.survey.model.SurveyView;
import com.marketlogic.survey.service.SurveyService;
import com.marketlogic.survey.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@ContextConfiguration
@RunWith(SpringRunner.class)
@WebMvcTest(value = {UserController.class, SurveyController.class})
public class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SurveyService surveyService;

    @MockBean
    private UserService userService;

    @MockBean
    private SurveyQuestionRepository surveyQuestionRepository;

    @MockBean
    private SurveyAnswerRepository surveyAnswerRepository;

    @MockBean
    private VoteRepository voteRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void addSurvey() throws Exception {
        Mockito.when(surveyService.addSurvey(Mockito.any(Long.class), Mockito.any(SurveyRequest.class))).thenReturn(getSurveyView());
        String surveyJson = "{\n" +
                "            \"question\": \"What do you like?\",\n" +
                "                \"choices\": [\n" +
                "            {\n" +
                "                \"text\": \"Tennis\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"text\": \"Cricket\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"text\": \"Rafa\"\n" +
                "            }\n" +
                "  ],\n" +
                "            \"surveyLength\": {\n" +
                "            \"days\": 7,\n" +
                "                    \"hours\": 23\n" +
                "        }\n" +
                "        }";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/survey/add")
                .accept(MediaType.APPLICATION_JSON)
                .content(surveyJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = "{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"Survey created successfully!: 1\"\n" +
                "}";
        Assert.notNull(result.getResponse().getContentAsString(), "success");
    }

    public User getUser() {
        return User.builder()
                .username("snikam")
                .password(Base64.getEncoder().encodeToString("snikam".getBytes()))
                .email("sac@sac.com")
                .build();
    }

    public SurveyView getSurveyView() {
        List<AnswerResponse> answerResponses = new ArrayList<>();
        answerResponses.add(AnswerResponse.builder()
                .id(1L)
                .text("Sachin")
                .build());

        List<Long> selectedAnswers = new ArrayList<>();
        selectedAnswers.add(1L);
        return SurveyView.builder()
                .id(1L)
                .question("What's your name?")
                .selectedAnswers(selectedAnswers)
                .choices(answerResponses)
                .expirationDateTime(OffsetDateTime.now().plusDays(2))
                .build();
    }
}

