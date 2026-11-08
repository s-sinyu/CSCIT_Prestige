package com.prestige.renovation.control.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.auth.ProfileUpdateRequest;
import com.prestige.renovation.dto.auth.UserSummary;
import com.prestige.renovation.entity.User;

/** Control for user story: Logged-in user edits own full name and phone; email and role cannot change (FR-AUTH-5). */
@Service
public class UpdateOwnProfileController {

    @Transactional
    public UserSummary update(Integer userId, ProfileUpdateRequest request) {
        User user = ControlSupport.requireActiveUser(userId);
        user.updateDetails(request.fullName().trim(), ControlSupport.clean(request.phone()));
        return UserSummary.from(user.save());
    }
}
