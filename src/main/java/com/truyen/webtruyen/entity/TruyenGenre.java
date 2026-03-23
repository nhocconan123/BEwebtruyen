package com.truyen.webtruyen.entity;

import com.truyen.webtruyen.entity.id.TruyenGenreId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "truyen_genres")
public class TruyenGenre {

    @EmbeddedId
    private TruyenGenreId id;

    public TruyenGenreId getId() {
        return id;
    }

    public void setId(TruyenGenreId id) {
        this.id = id;
    }
}
