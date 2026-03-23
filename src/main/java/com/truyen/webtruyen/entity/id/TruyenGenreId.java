package com.truyen.webtruyen.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TruyenGenreId implements Serializable {

    @Column(name = "truyen_id")
    private Long truyenId;

    @Column(name = "genre_id")
    private Long genreId;

    public TruyenGenreId() {
    }

    public TruyenGenreId(Long truyenId, Long genreId) {
        this.truyenId = truyenId;
        this.genreId = genreId;
    }

    public Long getTruyenId() {
        return truyenId;
    }

    public void setTruyenId(Long truyenId) {
        this.truyenId = truyenId;
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
        TruyenGenreId that = (TruyenGenreId) o;
        return Objects.equals(truyenId, that.truyenId) && Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(truyenId, genreId);
    }
}
