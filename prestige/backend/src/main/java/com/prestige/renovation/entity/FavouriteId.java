package com.prestige.renovation.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Composite primary key (customer, project) of {@link Favourite}. */
@Embeddable
public class FavouriteId implements Serializable {

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "project_id")
    private Integer projectId;

    protected FavouriteId() {
    }

    public FavouriteId(Integer customerId, Integer projectId) {
        this.customerId = customerId;
        this.projectId = projectId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FavouriteId other)) {
            return false;
        }
        return Objects.equals(customerId, other.customerId) && Objects.equals(projectId, other.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, projectId);
    }
}
