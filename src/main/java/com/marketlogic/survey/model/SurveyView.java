package com.marketlogic.survey.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SurveyView {
    private Long id;
    private String question;
    private List<AnswerResponse> choices;
    private UserView userView;
    private OffsetDateTime creationDateTime;
    private OffsetDateTime expirationDateTime;
    private Boolean isExpired;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> selectedAnswers;
    private Long totalVotes;
}
