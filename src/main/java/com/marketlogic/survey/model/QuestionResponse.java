package com.marketlogic.survey.model;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class QuestionResponse {

    private long id;
    private List<AnswerResponse> answerResponses;
}
