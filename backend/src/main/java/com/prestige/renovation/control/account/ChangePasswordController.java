package com.prestige.renovation.control.account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.auth.ChangePasswordRequest;
import com.prestige.renovation.entity.User;

/** Control for user story: Logged-in user changes own password, confirming the current one (FR-AUTH-5). */
@Service
public class ChangePasswordController {

    private final PasswordEncoder passwordEncoder;

    public ChangePasswordController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void changePassword(Integer userId, ChangePasswordRequest request) {
        User user = ControlSupport.requireActiveUser(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw ApiException.badRequest("currentPassword", "Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.save();
    }
}
