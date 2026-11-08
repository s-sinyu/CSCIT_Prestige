package com.prestige.renovation.control.favourite;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.entity.Favourite;

/** Control for user story: Customer removes an IDP from the favourite list (FR-CUS-4). Idempotent. */
@Service
public class RemoveFavouriteController {

    @Transactional
    public void remove(Integer customerId, Integer projectId) {
        Favourite.remove(customerId, projectId);
    }
}
