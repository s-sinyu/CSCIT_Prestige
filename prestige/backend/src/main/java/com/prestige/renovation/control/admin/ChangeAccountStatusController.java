package com.prestige.renovation.control.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.dto.admin.AdminUserResponse;
import com.prestige.renovation.entity.AccountStatus;
import com.prestige.renovation.entity.User;

/**
 * Control for user stories: User Admin suspends / reactivates an account (FR-ADM-5, FR-ADM-7).
 * A suspended designer's content disappears from public pages automatically through the visibility rule (FR-ADM-8).
 */
@Service
public class ChangeAccountStatusController {

    @Transactional
    public AdminUserResponse changeStatus(Integer adminId, Integer userId, AccountStatus status) {
        if (adminId.equals(userId)) {
            throw ApiException.badRequest("You cannot suspend or reactivate your own account");
        }
        User user = User.findById(userId).orElseThrow(() -> ApiException.notFound("Account not found"));
        if (user.isDeleted()) {
            throw ApiException.badRequest("Deleted accounts cannot be suspended or reactivated");
        }
        user.changeStatus(status);
        return AdminUserResponse.from(user.save());
    }
}
