package com.marketlogic.survey.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AnswerRequest {

    @NotBlank
    @Size(max = 40)
    private String text;
}
