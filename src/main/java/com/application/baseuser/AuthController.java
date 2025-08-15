package com.application.baseuser;

import com.application.baseuser.request.AuthorizationRequest;
import com.application.baseuser.request.RegisterUserRequest;
import com.application.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication APIs", description = "APIs for user and brand authentication, login, and registration.")
public class AuthController {


    private final AuthService authService;


    @GetMapping(path = "/ping")
    @Operation(summary = "Check Application Status", description = "Verify that the application is running smoothly.")
    public String ApplicationStatus() {
        return "Hi I am up!";
    }


    @PostMapping(path = "/login")
    @Operation(summary = "User Authentication", description = "Authenticate and log in a user (either a regular user or a brand).")
    public ResponseEntity<?> login(@RequestBody AuthorizationRequest authorizationRequest, @RequestHeader(required = false) Locale locale) {
        return new ResponseEntity<>(authService.login(authorizationRequest, locale), HttpStatus.OK);
    }

    @GetMapping("/refresh-token")
    @Operation(summary = "Refresh Token", description = "Refresh authentication token for both users and brands.")
    public ResponseEntity<Response> refreshToken(
            HttpServletRequest request, @RequestHeader(required = false) Locale locale) {
        return new ResponseEntity<>(authService.refreshToken(request, locale), HttpStatus.OK);
    }

    @GetMapping("/logout")
    @Operation(summary = "User Logout", description = "Log out both users and brands.")
    public ResponseEntity<Response> logout(
            HttpServletRequest request, @RequestHeader(required = false) Locale locale) {
        return new ResponseEntity<>(authService.logout(request, locale), HttpStatus.OK);
    }

    @GetMapping(path = "user/{token}/activate")
    @Operation(summary = "User activation", description = "Activate a previously signed up user")
    public ResponseEntity<Response> activateUser(@PathVariable String token, @RequestHeader(required = false) Locale locale) {
        return new ResponseEntity<>(authService.activateUser(token, locale), HttpStatus.OK);
    }

    @PostMapping(path = "/user/signup")
    @Operation(summary = "User Signup", description = "Register as a user.")
    public ResponseEntity<?> signup(@RequestBody RegisterUserRequest registerUserRequest,
                                    @RequestHeader(required = false) Locale locale) {
        return new ResponseEntity<>(authService.signup(registerUserRequest, locale), HttpStatus.OK);
    }
}
