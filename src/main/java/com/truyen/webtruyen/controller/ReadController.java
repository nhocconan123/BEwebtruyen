package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.Chapter;
import com.truyen.webtruyen.entity.Truyen;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.enums.StoryStatus;
import com.truyen.webtruyen.repository.ChapterRepository;
import com.truyen.webtruyen.repository.TruyenRepository;
import com.truyen.webtruyen.service.CurrentUserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/read")
public class ReadController {

    private final TruyenRepository truyenRepository;
    private final ChapterRepository chapterRepository;
    private final CurrentUserService currentUserService;

    public ReadController(TruyenRepository truyenRepository,
                          ChapterRepository chapterRepository,
                          CurrentUserService currentUserService) {
        this.truyenRepository = truyenRepository;
        this.chapterRepository = chapterRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/{slug}/chuong/{chapterNumber}")
    public Map<String, Object> read(@PathVariable String slug, @PathVariable Integer chapterNumber) {
        Truyen truyen = truyenRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
        requireVisible(truyen);

        Chapter chapter = chapterRepository.findByTruyenIdAndChapterNumber(truyen.getId(), chapterNumber)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Chapter not found"));

        Integer prev = chapterRepository
                .findFirstByTruyenIdAndChapterNumberLessThanOrderByChapterNumberDesc(truyen.getId(), chapterNumber)
                .map(Chapter::getChapterNumber)
                .orElse(null);

        Integer next = chapterRepository
                .findFirstByTruyenIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(truyen.getId(), chapterNumber)
                .map(Chapter::getChapterNumber)
                .orElse(null);

        // Map.of() does not allow null values; prev/next are nullable.
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("story", truyen);
        resp.put("chapter", chapter);
        resp.put("prevChapterNumber", prev);
        resp.put("nextChapterNumber", next);
        return resp;
    }

    private void requireVisible(Truyen truyen) {
        if (truyen == null) {
            throw new ResponseStatusException(NOT_FOUND, "Truyen not found");
        }
        if (StoryStatus.APPROVED.equals(truyen.getStatus())) {
            return;
        }

        User current = currentUserService.getCurrentUserOrNull();
        if (current == null) {
            throw new ResponseStatusException(NOT_FOUND, "Truyen not found");
        }
        if (currentUserService.isAdmin(current)) {
            return;
        }

        throw new ResponseStatusException(NOT_FOUND, "Truyen not found");
    }
}
