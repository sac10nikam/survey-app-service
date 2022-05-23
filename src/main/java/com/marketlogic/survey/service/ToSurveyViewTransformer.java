package com.marketlogic.survey.service;

import com.marketlogic.survey.data.SurveyQuestion;
import com.marketlogic.survey.data.User;
import com.marketlogic.survey.model.AnswerResponse;
import com.marketlogic.survey.model.SurveyView;
import com.marketlogic.survey.model.UserView;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ToSurveyViewTransformer {

    public static SurveyView transformDaoToSurveyView(SurveyQuestion surveyQuestion, User creator, List<Long> answersList) {

        List<AnswerResponse> answerResponses = surveyQuestion.getSurveyAnswers().stream().map(choice -> {
            AnswerResponse answerResponse = AnswerResponse.builder()
                    .id(choice.getId())
                    .text(choice.getText())
                    .build();
            return answerResponse;
        }).collect(Collectors.toList());

        UserView userView = UserView.builder()
                .id(creator.getId())
                .username(creator.getUsername())
                .name(creator.getName())
                .build();
        long totalVotes = answerResponses.stream().mapToLong(AnswerResponse::getVoteCount).sum();

        return SurveyView.builder()
                .id(surveyQuestion.getId())
                .question(surveyQuestion.getQuestion())
                .creationDateTime(surveyQuestion.getCreatedAt())
                .expirationDateTime(surveyQuestion.getExpirationDateTime())
                .choices(answerResponses)
                //.userView(userView)
                .selectedAnswers(answersList != null ? answersList : Collections.emptyList())
                .totalVotes(totalVotes)
                .build();
    }

}
