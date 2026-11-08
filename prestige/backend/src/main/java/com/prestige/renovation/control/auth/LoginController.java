package com.prestige.renovation.control.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.common.security.JwtService;
import com.prestige.renovation.dto.auth.LoginRequest;
import com.prestige.renovation.dto.auth.LoginResponse;
import com.prestige.renovation.dto.auth.UserSummary;
import com.prestige.renovation.entity.AccountStatus;
import com.prestige.renovation.entity.User;

/** Control for user story: User logs in with email and password and receives a JWT (FR-AUTH-3, FR-AUTH-4). */
@Service
public class LoginController {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginController(PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = User.findByEmail(request.email().trim())
                .filter(u -> !u.isDeleted())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));
        if (user.getStatus() == AccountStatus.SUSPENDED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "This account has been suspended. Please contact support.");
        }
        String token = jwtService.issue(user.getId(), user.getEmail(), user.getRole());
        return new LoginResponse(token, jwtService.lifetime().toSeconds(), UserSummary.from(user));
    }
}
