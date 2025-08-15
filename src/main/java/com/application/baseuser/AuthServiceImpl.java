package com.application.baseuser;

import com.application.baseuser.request.AuthorizationRequest;
import com.application.baseuser.request.RegisterUserRequest;
import com.application.baseuser.response.AuthenticationResponse;
import com.application.common.enums.RoleType;
import com.application.common.enums.URLS;
import com.application.common.exceptions.ApplicationException;
import com.application.common.exceptions.ApplicationError;
import com.application.common.response.Response;
import com.application.common.response.ResponseCode;
import com.application.config.jwt.JwtService;
import com.application.config.mail.EmailDetails;
import com.application.config.mail.EmailEncryptionUtil;
import com.application.config.mail.EmailServiceImpl;
import com.application.user.User;
import com.application.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static com.application.common.response.ResponseCode.BAD_USER_CREDENTIALS;
import static com.application.common.response.ResponseCode.LOGOUT_SUCCESSFULLY;
import static com.application.common.response.ResponseCode.REFRESH_TOKEN_FETCHED;
import static com.application.common.response.ResponseCode.SIGNUP_SUCCESS;
import static com.application.common.response.ResponseCode.SIGN_IN_SUCCESSFUL;
import static com.application.common.response.ResponseCode.TOKEN_NOT_VALID;
import static com.application.common.response.ResponseCode.USER_ACTIVATED_SUCCESSFULLY;
import static com.application.common.response.ResponseCode.USER_ALREADY_EXISTS;
import static com.application.common.response.ResponseCode.USER_DISABLED;
import static com.application.common.response.ResponseCode.USER_NOT_REGISTERED;
import static com.application.config.mail.EmailTemplate.ACTIVATE_USER;

@Service
public class AuthServiceImpl implements AuthService {


    @Value("${custom.url}")
    private String appUrl;


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BaseUserRepository baseUserRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private MessageSource messageSource;

    @Override
    public Response signup(RegisterUserRequest registerUserRequest, Locale locale) {

        return baseUserRepository.findByEmail(registerUserRequest.getEmail())
                .map(baseUser -> buildResponse(USER_ALREADY_EXISTS, locale))
                .orElseGet(() -> {
                    String encodedPassword = passwordEncoder.encode(registerUserRequest.getPassword());

                    BaseUser baseUser = BaseUser.builder()
                            .email(registerUserRequest.getEmail())
                            .password(encodedPassword)
                            .role(RoleType.USER.name())
                            .build();
                    baseUserRepository.save(baseUser);

                    User user = User.builder()
                            .firstName(registerUserRequest.getFirstName())
                            .lastName(registerUserRequest.getLastName())
                            .user(baseUser)
                            .build();
                    userRepository.save(user);
                    try {
                        sendActivationUserEmail(registerUserRequest.getEmail());
                    } catch (Exception e) {
                        return buildResponse(USER_NOT_REGISTERED, locale);
                    }
                    return buildResponse(SIGNUP_SUCCESS, locale);
                });
    }

    @Override
    public Response login(AuthorizationRequest registerRequest, Locale locale) {

        return baseUserRepository.findByEmail(registerRequest.getEmail())
                .map(user -> {
                    if (!user.isEnabled()) {
                        return buildResponse(USER_DISABLED, locale);
                    }
                    try {
                        authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        registerRequest.getEmail(),
                                        registerRequest.getPassword()
                                ));
                        return buildResponseWithData(
                                AuthenticationResponse.builder()
                                        .accessToken(jwtService.generateAccessToken(user))
                                        .refreshToken(jwtService.generateRefreshToken(user))
                                        .build(),
                                SIGN_IN_SUCCESSFUL, locale
                        );
                    } catch (AuthenticationException e) {
                        return buildResponse(BAD_USER_CREDENTIALS, locale);
                    }
                })
                .orElseGet(() ->
                        buildResponse(USER_NOT_REGISTERED, locale)
                );
    }

    @Override
    public Response refreshToken(HttpServletRequest request, Locale locale) {

        String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String email = jwtService.extractUsername(token);
        return baseUserRepository.findByEmail(email)
                .map(baseUser -> {
                    jwtService.revokeTokens(baseUser.getEmail());
                    return buildResponseWithData(
                            AuthenticationResponse.builder()
                                    .accessToken(jwtService.generateAccessToken(baseUser))
                                    .refreshToken(jwtService.generateRefreshToken(baseUser))
                                    .build(),
                            REFRESH_TOKEN_FETCHED, locale
                    );
                })
                .orElseGet(() -> buildResponse(TOKEN_NOT_VALID, locale));
    }

    @Override
    public Response logout(HttpServletRequest request, Locale locale) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String email = jwtService.extractUsername(token);
        return baseUserRepository.findByEmail(email)
                .map(baseUser -> {
                    jwtService.revokeTokens(baseUser.getEmail());
                    return buildResponse(LOGOUT_SUCCESSFULLY, locale);
                })
                .orElseGet(() -> buildResponse(TOKEN_NOT_VALID, locale));
    }

    @SneakyThrows
    @Override
    public Response activateUser(String token, Locale locale) {
        String email = EmailEncryptionUtil.decrypt(token);
        return baseUserRepository.findByEmail(email)
                .map(baseUser -> {
                    baseUser.setActive(Boolean.TRUE);
                    baseUserRepository.save(baseUser);
                    return buildResponse(USER_ACTIVATED_SUCCESSFULLY, locale);
                })
                .orElseGet(() -> buildResponse(ResponseCode.USER_NOT_REGISTERED, locale));
    }

    @Async
    protected void sendActivationUserEmail(String toEmail) {
        try {
            String encryptedEmail = EmailEncryptionUtil.encrypt(toEmail);
            String activationLink = appUrl + URLS.USER_ACTIVATION.format(encryptedEmail);
            String emailBody = String.format(ACTIVATE_USER.getBodyTemplate(), activationLink);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(toEmail)
                    .subject(ACTIVATE_USER.getName())
                    .msgBody(emailBody)
                    .build();

            emailService.sendMail(emailDetails);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.EMAIL_SENDING_FAILED);
        }
    }

    Response buildResponse(ResponseCode code, Locale locale) {
        return Response.builder()
                .code(Integer.valueOf(code.getCode()))
                .message(messageSource.getMessage(String.valueOf(Integer.valueOf(code.getCode())), null, locale))
                .build();
    }

    public <T> Response buildResponseWithData(T data, ResponseCode code, Locale locale) {
        return Response.builder()
                .data(data)
                .code(Integer.valueOf(code.getCode()))
                .message(messageSource.getMessage(String.valueOf(Integer.valueOf(code.getCode())), null, locale))
                .build();
    }
}