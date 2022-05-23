package com.marketlogic.survey.exception;

import static java.lang.String.format;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message, Object... arguments) {
        super(format(message, arguments));
    }
}
