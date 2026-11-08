package com.prestige.renovation.control.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.control.common.ControlSupport;
import com.prestige.renovation.dto.auth.UserSummary;

/** Control for user story: Logged-in user views own profile (FR-AUTH-5). */
@Service
public class ViewOwnProfileController {

    @Transactional(readOnly = true)
    public UserSummary view(Integer userId) {
        return UserSummary.from(ControlSupport.requireActiveUser(userId));
    }
}
