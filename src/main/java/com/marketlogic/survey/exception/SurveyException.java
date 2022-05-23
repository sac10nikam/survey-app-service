package com.marketlogic.survey.exception;

import static java.lang.String.format;

public class SurveyException extends RuntimeException {

    public SurveyException(String message, Object... arguments) {
        super(format(message, arguments));
    }
}
