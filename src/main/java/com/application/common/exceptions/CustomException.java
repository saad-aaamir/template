package com.application.common.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = -821615227590605886L;

    private Integer errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    public CustomException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public CustomException(int errorCode, String message, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public CustomException(ApplicationError applicationError, HttpStatus httpStatus) {
        this.errorCode = applicationError.getErrorCode();
        this.message = applicationError.getMessage();
        this.httpStatus = httpStatus;
    }
}
