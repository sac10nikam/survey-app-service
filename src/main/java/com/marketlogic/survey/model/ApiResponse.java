package com.marketlogic.survey.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ApiResponse {

    private Boolean success;
    private String message;
}
