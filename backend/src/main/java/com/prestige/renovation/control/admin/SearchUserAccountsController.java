package com.prestige.renovation.control.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.admin.AdminUserResponse;
import com.prestige.renovation.entity.AccountStatus;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/** Control for user story: User Admin searches accounts by name/email and filters by role and status (FR-ADM-2). */
@Service
public class SearchUserAccountsController {

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> search(String keyword, Role role, AccountStatus status,
                                                  boolean includeDeleted, Pageable pageable) {
        return PageResponse.of(User.search(keyword, role, status, includeDeleted, pageable), AdminUserResponse::from);
    }
}
