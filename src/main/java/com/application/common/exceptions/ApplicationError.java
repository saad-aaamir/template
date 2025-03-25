package com.application.common.exceptions;

import lombok.Getter;

@Getter
public enum ApplicationError {
    EMAIL_ALREADY_EXISTS_MSG(2002, "Email already exists!"),
    USER_NOT_REGISTERED(2004, "User not registered!"),
    BRAND_NOT_REGISTERED(2005, "Brand not registered!"),
    TOKEN_NOT_VALID(2006, "Token not valid!"),
    USER_DISABLED(2007, "User is disabled!"),
    EMAIL_SENDING_FAILED(2008, "Unable to send email");;


    private int errorCode;
    private String message;

    ApplicationError(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
