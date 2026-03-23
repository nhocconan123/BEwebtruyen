package com.truyen.webtruyen.dto.admin;

import com.truyen.webtruyen.entity.enums.PublishStatus;
import com.truyen.webtruyen.entity.enums.StoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class AdminUpdateTruyenRequest {

    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title max length is 255")
    private String title;

    @Size(max = 255, message = "slug max length is 255")
    private String slug;

    @Size(max = 10000, message = "description max length is 10000")
    private String description;

    @Size(max = 255, message = "coverImage max length is 255")
    private String coverImage;

    private Long authorId;

    @Size(max = 255, message = "authorName max length is 255")
    private String authorName;

    private StoryStatus status;

    private PublishStatus publishStatus;

    private Long viewCount;

    private LocalDateTime approvedAt;

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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public StoryStatus getStatus() {
        return status;
    }

    public void setStatus(StoryStatus status) {
        this.status = status;
    }

    public PublishStatus getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(PublishStatus publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
}
