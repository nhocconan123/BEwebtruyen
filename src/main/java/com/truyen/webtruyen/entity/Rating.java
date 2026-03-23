package com.truyen.webtruyen.entity;

import com.truyen.webtruyen.entity.id.RatingId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ratings")
public class Rating {

    @EmbeddedId
    private RatingId id;

    private Integer rating;

    public RatingId getId() {
        return id;
    }

    public void setId(RatingId id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
