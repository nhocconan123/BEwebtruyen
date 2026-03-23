package com.truyen.webtruyen.entity;

import com.truyen.webtruyen.entity.id.ReadingHistoryId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "reading_history")
public class ReadingHistory {

    @EmbeddedId
    private ReadingHistoryId id;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    public ReadingHistoryId getId() {
        return id;
    }

    public void setId(ReadingHistoryId id) {
        this.id = id;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
