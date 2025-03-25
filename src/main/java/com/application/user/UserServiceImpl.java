package com.application.user;

import com.application.baseUser.BaseUserRepository;
import com.application.config.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final JwtService jwtService;
    private final BaseUserRepository baseUserRepository;


}