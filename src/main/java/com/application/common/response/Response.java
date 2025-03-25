package com.application.common.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private Object data;
    private String message;
    private Integer code;

    public static Response of(Object data) {
        Response authResponse = new Response();
        authResponse.setData(data);
        return authResponse;
    }

    public static Response of(Object data, String message) {
        Response authResponse = new Response();
        authResponse.setData(data);
        authResponse.setMessage(message);
        return authResponse;
    }

    public static Response of(String messageCode, String message) {
        Response authResponse = new Response();
        authResponse.setCode(Integer.parseInt(messageCode));
        authResponse.setMessage(message);
        return authResponse;
    }

    public static Response of(Object data, String messageCode, String message) {
        Response authResponse = new Response();
        authResponse.setData(data);
        authResponse.setCode(Integer.parseInt(messageCode));
        authResponse.setMessage(message);
        return authResponse;
    }
}
