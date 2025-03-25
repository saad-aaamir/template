package com.application.baseUser;

import com.application.baseUser.request.AuthorizationRequest;
import com.application.baseUser.request.RegisterUserRequest;
import com.application.common.response.Response;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

public interface AuthService {

    //

    Response login(AuthorizationRequest registerRequest, Locale locale);

    Response signup(RegisterUserRequest registerUserRequest, Locale locale);

    Response logout(HttpServletRequest request, Locale locale);

    Response refreshToken(HttpServletRequest request, Locale locale);

    Response activateUser(String token, Locale locale);
}
