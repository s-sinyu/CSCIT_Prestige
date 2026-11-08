package com.prestige.renovation.control.admin;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.admin.AdminUpdateUserRequest;
import com.prestige.renovation.dto.admin.AdminUserResponse;
import com.prestige.renovation.entity.User;

/** Control for user story: User Admin edits an account's full name, phone and email (FR-ADM-4). */
@Service
public class UpdateUserAccountController {

    @Transactional
    public AdminUserResponse update(Integer userId, AdminUpdateUserRequest request) {
        User user = User.findById(userId).orElseThrow(() -> ApiException.notFound("Account not found"));
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (User.emailTakenByOther(email, userId)) {
            throw ApiException.conflict("email", "Another account already uses this email");
        }
        user.setEmail(email);
        user.updateDetails(request.fullName().trim(), ControlSupport.clean(request.phone()));
        return AdminUserResponse.from(user.save());
    }
}
