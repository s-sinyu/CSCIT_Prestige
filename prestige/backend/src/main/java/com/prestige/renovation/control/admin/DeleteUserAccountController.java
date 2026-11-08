package com.prestige.renovation.control.admin;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.entity.User;

/**
 * Control for user story: User Admin deletes an account (FR-ADM-6, FR-ADM-7, FR-ADM-9).
 * Soft delete: the row and all related data stay; the email remains reserved.
 */
@Service
public class DeleteUserAccountController {

    private final Clock clock;

    public DeleteUserAccountController(Clock clock) {
        this.clock = clock;
    }

    @Transactional
    public void delete(Integer adminId, Integer userId) {
        if (adminId.equals(userId)) {
            throw ApiException.badRequest("You cannot delete your own account");
        }
        User user = User.findById(userId).orElseThrow(() -> ApiException.notFound("Account not found"));
        if (!user.isDeleted()) {
            user.softDelete(clock.instant());
            user.save();
        }
    }
}
