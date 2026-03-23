package com.truyen.webtruyen.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserGenreId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "genre_id")
    private Long genreId;

    public UserGenreId() {
    }

    public UserGenreId(Long userId, Long genreId) {
        this.userId = userId;
        this.genreId = genreId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGenreId that = (UserGenreId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, genreId);
    }
}
