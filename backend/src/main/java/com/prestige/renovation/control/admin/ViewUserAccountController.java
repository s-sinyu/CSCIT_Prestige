package com.prestige.renovation.control.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.dto.admin.AdminUserResponse;
import com.prestige.renovation.entity.User;

/** Control for user story: User Admin views one account's details (FR-ADM-3). */
@Service
public class ViewUserAccountController {

    @Transactional(readOnly = true)
    public AdminUserResponse view(Integer userId) {
        return User.findById(userId).map(AdminUserResponse::from)
                .orElseThrow(() -> ApiException.notFound("Account not found"));
    }
}
