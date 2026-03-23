package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.dto.admin.AdminUpdateTruyenRequest;
import com.truyen.webtruyen.entity.Truyen;
import com.truyen.webtruyen.entity.enums.StoryStatus;
import com.truyen.webtruyen.repository.ChapterRepository;
import com.truyen.webtruyen.repository.CommentRepository;
import com.truyen.webtruyen.repository.FavoriteRepository;
import com.truyen.webtruyen.repository.RatingRepository;
import com.truyen.webtruyen.repository.ReadingHistoryRepository;
import com.truyen.webtruyen.repository.TruyenGenreRepository;
import com.truyen.webtruyen.repository.TruyenRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/admin/truyen")
public class AdminTruyenController {

    private final TruyenRepository truyenRepository;
    private final ChapterRepository chapterRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final RatingRepository ratingRepository;
    private final TruyenGenreRepository truyenGenreRepository;
    private final ReadingHistoryRepository readingHistoryRepository;

    public AdminTruyenController(TruyenRepository truyenRepository,
                                 ChapterRepository chapterRepository,
                                 CommentRepository commentRepository,
                                 FavoriteRepository favoriteRepository,
                                 RatingRepository ratingRepository,
                                 TruyenGenreRepository truyenGenreRepository,
                                 ReadingHistoryRepository readingHistoryRepository) {
        this.truyenRepository = truyenRepository;
        this.chapterRepository = chapterRepository;
        this.commentRepository = commentRepository;
        this.favoriteRepository = favoriteRepository;
        this.ratingRepository = ratingRepository;
        this.truyenGenreRepository = truyenGenreRepository;
        this.readingHistoryRepository = readingHistoryRepository;
    }

    @GetMapping
    public Page<Truyen> getAll(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "createdAt") String sortBy,
                               @RequestParam(defaultValue = "desc") String sortDir,
                               @RequestParam(required = false) StoryStatus status,
                               @RequestParam(required = false) Long authorId,
                               @RequestParam(required = false) String keyword) {
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Truyen> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (authorId != null) {
                predicates.add(cb.equal(root.get("authorId"), authorId));
            }
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return truyenRepository.findAll(spec, pageable);
    }

    @GetMapping("/pending")
    public Page<Truyen> getPending(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return truyenRepository.findAll((root, query, cb) -> cb.equal(root.get("status"), StoryStatus.PENDING), pageable);
    }

    @GetMapping("/{id}")
    public Truyen getById(@PathVariable Long id) {
        return truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
    }

    @PutMapping("/{id}")
    public Truyen update(@PathVariable Long id, @Valid @RequestBody AdminUpdateTruyenRequest payload) {
        Truyen truyen = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));

        truyen.setTitle(payload.getTitle());
        truyen.setSlug(payload.getSlug());
        truyen.setDescription(payload.getDescription());
        truyen.setCoverImage(payload.getCoverImage());

        if (payload.getAuthorId() != null) {
            truyen.setAuthorId(payload.getAuthorId());
        }
        if (payload.getAuthorName() != null) {
            String trimmed = payload.getAuthorName().trim();
            truyen.setAuthorName(trimmed.isEmpty() ? null : trimmed);
        }
        if (payload.getStatus() != null) {
            truyen.setStatus(payload.getStatus());
        }
        if (payload.getPublishStatus() != null) {
            truyen.setPublishStatus(payload.getPublishStatus());
        }
        if (payload.getViewCount() != null) {
            truyen.setViewCount(payload.getViewCount());
        }
        truyen.setApprovedAt(payload.getApprovedAt());

        return truyenRepository.save(truyen);
    }

    @PutMapping("/{id}/approve")
    public Truyen approve(@PathVariable Long id) {
        Truyen truyen = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));

        truyen.setStatus(StoryStatus.APPROVED);
        truyen.setApprovedAt(LocalDateTime.now());
        return truyenRepository.save(truyen);
    }

    @PutMapping("/{id}/reject")
    public Truyen reject(@PathVariable Long id) {
        Truyen truyen = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));

        truyen.setStatus(StoryStatus.REJECTED);
        truyen.setApprovedAt(null);
        return truyenRepository.save(truyen);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void hardDelete(@PathVariable Long id) {
        Truyen truyen = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));

        List<Long> chapterIds = chapterRepository.findByTruyenIdOrderByChapterNumberAsc(id)
                .stream()
                .map(ch -> ch.getId())
                .filter(chId -> chId != null)
                .toList();

        if (!chapterIds.isEmpty()) {
            readingHistoryRepository.deleteByIdChapterIdIn(chapterIds);
        }

        commentRepository.deleteByTruyenId(id);
        favoriteRepository.deleteByIdTruyenId(id);
        ratingRepository.deleteByIdTruyenId(id);
        truyenGenreRepository.deleteByIdTruyenId(id);
        chapterRepository.deleteByTruyenId(id);
        truyenRepository.delete(truyen);
    }
}
