package com.truyen.webtruyen.entity;

import com.truyen.webtruyen.entity.id.FavoriteId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public FavoriteId getId() {
        return id;
    }

    public void setId(FavoriteId id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
