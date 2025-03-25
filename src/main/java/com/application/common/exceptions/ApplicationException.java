package com.application.common.exceptions;

import org.springframework.http.HttpStatus;

public class ApplicationException extends CustomException {

    private static final long serialVersionUID = -95588956046956053L;

    public ApplicationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ApplicationException(ApplicationError applicationError) {
        super(applicationError, HttpStatus.BAD_REQUEST);
    }

    public static ApplicationException of(String message) {
        return new ApplicationException(message);
    }

    public static ApplicationException of(ApplicationError applicationError) {
        return new ApplicationException(applicationError);
    }
}
