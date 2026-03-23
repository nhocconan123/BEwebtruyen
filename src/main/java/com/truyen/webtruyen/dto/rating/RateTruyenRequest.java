package com.truyen.webtruyen.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RateTruyenRequest {
    @NotNull(message = "ratingValue is required")
    @Min(value = 1, message = "ratingValue must be between 1 and 5")
    @Max(value = 5, message = "ratingValue must be between 1 and 5")
    private Integer ratingValue;

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }
}

