package com.prestige.renovation.boundary.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prestige.renovation.control.admin.SearchUserAccountsController;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.admin.AdminUserResponse;
import com.prestige.renovation.entity.AccountStatus;
import com.prestige.renovation.entity.Role;

/** Boundary for user story: User Admin searches and filters accounts. */
@RestController
public class SearchUserAccountsBoundary {

    private final SearchUserAccountsController controller;

    public SearchUserAccountsBoundary(SearchUserAccountsController controller) {
        this.controller = controller;
    }

    @GetMapping("/api/admin/users")
    public PageResponse<AdminUserResponse> search(@RequestParam(required = false) String q,
                                                  @RequestParam(required = false) Role role,
                                                  @RequestParam(required = false) AccountStatus status,
                                                  @RequestParam(defaultValue = "false") boolean includeDeleted,
                                                  @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return controller.search(q, role, status, includeDeleted, pageable);
    }
}
