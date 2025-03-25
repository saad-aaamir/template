package com.application.common.response;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SIGNUP_SUCCESS("1001"),
    USER_ALREADY_EXISTS("1002"),
    SIGN_IN_SUCCESSFUL("1003"),
    USER_NOT_REGISTERED("1004"),
    BAD_USER_CREDENTIALS("1005"),
    REFRESH_TOKEN_FETCHED("1006"),
    TOKEN_NOT_VALID("1007"),
    LOGOUT_SUCCESSFULLY("1008"),
    USER_DISABLED("1009"),
    USER_ACTIVATED_SUCCESSFULLY("1010"),
    USER_SIGN_UP_FAILED("1011");

    private final String code;

    ResponseCode(String code) {
        this.code = code;
    }

}
