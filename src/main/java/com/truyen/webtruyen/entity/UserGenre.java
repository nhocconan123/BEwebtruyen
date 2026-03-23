package com.truyen.webtruyen.entity;

import com.truyen.webtruyen.entity.id.UserGenreId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_genres")
public class UserGenre {

    @EmbeddedId
    private UserGenreId id;

    public UserGenreId getId() {
        return id;
    }

    public void setId(UserGenreId id) {
        this.id = id;
    }
}
