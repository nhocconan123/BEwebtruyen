package com.truyen.webtruyen.dto.rating;

public class RatingSummaryResponse {
    private Long truyenId;
    private Double average;
    private Integer averageRounded;
    private Long count;
    private Integer myRating;

    public Long getTruyenId() {
        return truyenId;
    }

    public void setTruyenId(Long truyenId) {
        this.truyenId = truyenId;
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Integer getAverageRounded() {
        return averageRounded;
    }

    public void setAverageRounded(Integer averageRounded) {
        this.averageRounded = averageRounded;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Integer getMyRating() {
        return myRating;
    }

    public void setMyRating(Integer myRating) {
        this.myRating = myRating;
    }
}

