package com.truyen.webtruyen.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateCommentRequest {

    @NotNull(message = "truyenId is required")
    private Long truyenId;

    private Long chapterId;

    @NotBlank(message = "content is required")
    @Size(min = 1, max = 2000, message = "content must be between 1 and 2000 characters")
    private String content;

    public Long getTruyenId() {
        return truyenId;
    }

    public void setTruyenId(Long truyenId) {
        this.truyenId = truyenId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
