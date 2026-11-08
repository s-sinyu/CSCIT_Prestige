package com.prestige.renovation.control.favourite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.control.common.ProjectCardAssembler;
import com.prestige.renovation.dto.PageResponse;
import com.prestige.renovation.dto.project.ProjectCard;
import com.prestige.renovation.entity.Favourite;

/** Control for user story: Customer views the favourite list, newest first; hidden IDPs are left out (FR-CUS-4). */
@Service
public class ViewFavouritesController {

    @Transactional(readOnly = true)
    public PageResponse<ProjectCard> list(Integer customerId, Pageable pageable) {
        Pageable newestFirst = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "savedAt"));
        Page<Favourite> page = Favourite.findVisibleForCustomer(customerId, newestFirst);
        return PageResponse.of(page, ProjectCardAssembler.toCards(
                page.getContent().stream().map(Favourite::getProject).toList()));
    }
}
