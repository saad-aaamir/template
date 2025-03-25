package com.application.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum URLS {

    USER_ACTIVATION("/auth/user/%s/activate");


    private final String url;

    public String format(Object... args) {
        return String.format(this.url, args);
    }

}
