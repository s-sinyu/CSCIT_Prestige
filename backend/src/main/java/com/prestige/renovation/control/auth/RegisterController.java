package com.prestige.renovation.control.auth;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.auth.RegisterRequest;
import com.prestige.renovation.dto.auth.UserSummary;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/** Control for user story: Visitor registers as a Customer or Interior Designer (FR-AUTH-1). */
@Service
public class RegisterController {

    private final PasswordEncoder passwordEncoder;

    public RegisterController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserSummary register(RegisterRequest request) {
        if (request.role() != Role.CUSTOMER && request.role() != Role.INTERIOR_DESIGNER) {
            throw ApiException.badRequest("role", "You can only register as a Customer or an Interior Designer");
        }
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (User.emailTaken(email)) {
            throw ApiException.conflict("email", "An account with this email already exists");
        }
        User user = new User(email, passwordEncoder.encode(request.password()), request.fullName().trim(),
                ControlSupport.clean(request.phone()), request.role());
        return UserSummary.from(user.save());
    }
}
