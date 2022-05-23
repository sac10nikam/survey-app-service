package com.marketlogic.survey.model;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserView {

    private Long id;
    private String username;
    private String name;
    private OffsetDateTime joinedAt;
    private Long surveyCount;
    private Long voteCount;
}
