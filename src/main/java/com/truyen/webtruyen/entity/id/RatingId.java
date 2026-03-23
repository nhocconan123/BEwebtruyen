package com.truyen.webtruyen.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RatingId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "truyen_id")
    private Long truyenId;

    public RatingId() {
    }

    public RatingId(Long userId, Long truyenId) {
        this.userId = userId;
        this.truyenId = truyenId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTruyenId() {
        return truyenId;
    }

    public void setTruyenId(Long truyenId) {
        this.truyenId = truyenId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatingId ratingId = (RatingId) o;
        return Objects.equals(userId, ratingId.userId) && Objects.equals(truyenId, ratingId.truyenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, truyenId);
    }
}
