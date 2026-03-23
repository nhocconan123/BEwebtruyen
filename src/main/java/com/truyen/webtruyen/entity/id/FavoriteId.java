package com.truyen.webtruyen.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FavoriteId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "truyen_id")
    private Long truyenId;

    public FavoriteId() {
    }

    public FavoriteId(Long userId, Long truyenId) {
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
        FavoriteId that = (FavoriteId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(truyenId, that.truyenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, truyenId);
    }
}
