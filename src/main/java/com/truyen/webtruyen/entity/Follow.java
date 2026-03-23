package com.truyen.webtruyen.entity;

import com.truyen.webtruyen.entity.id.FollowId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "follows")
public class Follow {

    @EmbeddedId
    private FollowId id;

    public FollowId getId() {
        return id;
    }

    public void setId(FollowId id) {
        this.id = id;
    }
}
