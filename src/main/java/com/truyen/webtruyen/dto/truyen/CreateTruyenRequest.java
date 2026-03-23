package com.truyen.webtruyen.dto.truyen;

import com.truyen.webtruyen.entity.enums.PublishStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateTruyenRequest {

    @NotBlank(message = "title is required")
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

    /**
     * Optional genre ids to attach to the story on creation.
     * If provided:
     * - empty list means no genres
     * - otherwise attach these genres
     */
    private List<Long> genreIds;

    private PublishStatus publishStatus;

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

    public List<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public PublishStatus getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(PublishStatus publishStatus) {
        this.publishStatus = publishStatus;
    }
}
