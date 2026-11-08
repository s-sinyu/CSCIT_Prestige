package com.prestige.renovation.control.admin;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.admin.AdminUserResponse;
import com.prestige.renovation.dto.admin.CreateManagerRequest;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/** Control for user story: User Admin creates a Platform Management account (FR-ADM-1). */
@Service
public class CreatePlatformManagerController {

    private final PasswordEncoder passwordEncoder;

    public CreatePlatformManagerController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AdminUserResponse create(Integer adminId, CreateManagerRequest request) {
        ControlSupport.requireRole(adminId, Role.USER_ADMIN);
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (User.emailTaken(email)) {
            throw ApiException.conflict("email", "An account with this email already exists");
        }
        User user = new User(email, passwordEncoder.encode(request.password()), request.fullName().trim(),
                ControlSupport.clean(request.phone()), Role.PLATFORM_MANAGEMENT);
        return AdminUserResponse.from(user.save());
    }
}
