package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.Chapter;
import com.truyen.webtruyen.entity.Truyen;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.enums.StoryStatus;
import com.truyen.webtruyen.repository.ChapterRepository;
import com.truyen.webtruyen.repository.TruyenRepository;
import com.truyen.webtruyen.service.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterRepository chapterRepository;
    private final TruyenRepository truyenRepository;
    private final CurrentUserService currentUserService;

    public ChapterController(ChapterRepository chapterRepository,
                             TruyenRepository truyenRepository,
                             CurrentUserService currentUserService) {
        this.chapterRepository = chapterRepository;
        this.truyenRepository = truyenRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public Page<Chapter> getAll(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) Long truyenId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("chapterNumber").ascending());
        if (truyenId != null) {
            Truyen truyen = truyenRepository.findById(truyenId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
            requireVisible(truyen);
            return chapterRepository.findByTruyenId(truyenId, pageable);
        }
        return chapterRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Chapter getById(@PathVariable Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Chapter not found"));
        Truyen truyen = truyenRepository.findById(chapter.getTruyenId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
        requireVisible(truyen);
        return chapter;
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

    @PostMapping
    public Chapter create(@RequestBody Chapter chapter) {
        chapter.setId(null);
        return chapterRepository.save(chapter);
    }

    @PutMapping("/{id}")
    public Chapter update(@PathVariable Long id, @RequestBody Chapter chapter) {
        if (!chapterRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Chapter not found");
        }
        chapter.setId(id);
        return chapterRepository.save(chapter);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (!chapterRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Chapter not found");
        }
        chapterRepository.deleteById(id);
    }
}
