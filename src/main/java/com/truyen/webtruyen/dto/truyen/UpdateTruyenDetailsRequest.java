package com.truyen.webtruyen.dto.truyen;

import com.truyen.webtruyen.entity.enums.PublishStatus;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Partial update payload for story "details".
 * All fields are optional; null means "do not change".
 */
public class UpdateTruyenDetailsRequest {

    @Size(max = 255, message = "title max length is 255")
    private String title;

    @Size(max = 255, message = "slug max length is 255")
    private String slug;

    @Size(max = 10000, message = "description max length is 10000")
    private String description;

    @Size(max = 255, message = "coverImage max length is 255")
    private String coverImage;

    @Size(max = 255, message = "authorName max length is 255")
    private String authorName;

    private PublishStatus publishStatus;

    /**
     * If provided:
     * - empty list means remove all genres from the story
     * - otherwise replace story genres by this list
     */
    private List<Long> genreIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public PublishStatus getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(PublishStatus publishStatus) {
        this.publishStatus = publishStatus;
    }

    public List<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }
}
