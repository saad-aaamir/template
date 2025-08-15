package com.application.config.jwt;

import com.application.baseuser.BaseUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class JwtUtils {

    public JwtUtils(JwtService jwtTokenService) {
    }

    public String getCurrentUserUuid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof BaseUser baseUser) {
            return baseUser.getUuid(); // UUID directly from your user class
        }

        throw new AccessDeniedException("User UUID not found in authentication");
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof BaseUser baseUser) {
            return baseUser.getRole(); // UUID directly from your user class
        }

        throw new AccessDeniedException("User UUID not found in authentication");
    }
}
