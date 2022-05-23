package com.marketlogic.survey.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AnswerResponse {

    private long id;
    private String text;
    private long voteCount;
}
