package com.truyen.webtruyen.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReadingHistoryId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "chapter_id")
    private Long chapterId;

    public ReadingHistoryId() {
    }

    public ReadingHistoryId(Long userId, Long chapterId) {
        this.userId = userId;
        this.chapterId = chapterId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReadingHistoryId that = (ReadingHistoryId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(chapterId, that.chapterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, chapterId);
    }
}
