package com.example.dentalscheduler.security.util;

import com.example.dentalscheduler.model.User;
import com.example.dentalscheduler.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    private static String jwtToken;

    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        return customUserDetails.getId();
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        return customUserDetails.getUser();
    }

    public static void setJwtToken(String jwtToken) {
        SecurityUtils.jwtToken = jwtToken;
    }

    public static String getJwtToken() {
        return SecurityUtils.jwtToken;
    }
}
