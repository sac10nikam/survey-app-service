package com.marketlogic.survey.data;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SurveyAnswerVoteCount {

    private Long answerId;
    private Long voteCount;
}

