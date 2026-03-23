package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.dto.truyen.CreateTruyenRequest;
import com.truyen.webtruyen.dto.truyen.UpdateTruyenDetailsRequest;
import com.truyen.webtruyen.dto.truyen.UpdateTruyenRequest;
import com.truyen.webtruyen.entity.Chapter;
import com.truyen.webtruyen.entity.Genre;
import com.truyen.webtruyen.entity.Truyen;
import com.truyen.webtruyen.entity.TruyenGenre;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.id.TruyenGenreId;
import com.truyen.webtruyen.entity.enums.PublishStatus;
import com.truyen.webtruyen.entity.enums.StoryStatus;
import com.truyen.webtruyen.repository.ChapterRepository;
import com.truyen.webtruyen.repository.CommentRepository;
import com.truyen.webtruyen.repository.FavoriteRepository;
import com.truyen.webtruyen.repository.GenreRepository;
import com.truyen.webtruyen.repository.RatingRepository;
import com.truyen.webtruyen.repository.ReadingHistoryRepository;
import com.truyen.webtruyen.repository.TruyenGenreRepository;
import com.truyen.webtruyen.repository.TruyenRepository;
import com.truyen.webtruyen.service.CurrentUserService;
import com.truyen.webtruyen.util.SlugUtil;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/truyen")
public class TruyenController {

    private final TruyenRepository truyenRepository;
    private final CurrentUserService currentUserService;
    private final ChapterRepository chapterRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final RatingRepository ratingRepository;
    private final TruyenGenreRepository truyenGenreRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final GenreRepository genreRepository;

    public TruyenController(TruyenRepository truyenRepository,
                            CurrentUserService currentUserService,
                            ChapterRepository chapterRepository,
                            CommentRepository commentRepository,
                            FavoriteRepository favoriteRepository,
                            RatingRepository ratingRepository,
                            TruyenGenreRepository truyenGenreRepository,
                            ReadingHistoryRepository readingHistoryRepository,
                            GenreRepository genreRepository) {
        this.truyenRepository = truyenRepository;
        this.currentUserService = currentUserService;
        this.chapterRepository = chapterRepository;
        this.commentRepository = commentRepository;
        this.favoriteRepository = favoriteRepository;
        this.ratingRepository = ratingRepository;
        this.truyenGenreRepository = truyenGenreRepository;
        this.readingHistoryRepository = readingHistoryRepository;
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public Page<Truyen> getAll(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "createdAt") String sortBy,
                               @RequestParam(defaultValue = "desc") String sortDir,
                               @RequestParam(required = false) StoryStatus status,
                               @RequestParam(required = false) PublishStatus publishStatus,
                               @RequestParam(required = false) Long genreId,
                               @RequestParam(required = false) Long authorId,
                               @RequestParam(required = false) String keyword) {
        // Public listing: only APPROVED stories are visible unless admin explicitly requests another status.
        User current = currentUserService.getCurrentUserOrNull();
        boolean isAdmin = current != null && currentUserService.isAdmin(current);
        StoryStatus effectiveStatus = (isAdmin && status != null) ? status : StoryStatus.APPROVED;

        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Truyen> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (effectiveStatus != null) {
                predicates.add(cb.equal(root.get("status"), effectiveStatus));
            }
            if (publishStatus != null) {
                predicates.add(cb.equal(root.get("publishStatus"), publishStatus));
            }
            if (authorId != null) {
                predicates.add(cb.equal(root.get("authorId"), authorId));
            }
            if (genreId != null) {
                var sq = query.subquery(Long.class);
                var tg = sq.from(TruyenGenre.class);
                sq.select(tg.get("id").get("truyenId"))
                        .where(cb.equal(tg.get("id").get("genreId"), genreId));
                predicates.add(root.get("id").in(sq));
            }
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return truyenRepository.findAll(spec, pageable);
    }

    @GetMapping({"/the-loai/{genreSlug}", "/by-genre/{genreSlug}"})
    public Page<Truyen> getByGenreSlug(@PathVariable String genreSlug,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "createdAt") String sortBy,
                                       @RequestParam(defaultValue = "desc") String sortDir) {
        String slug = (genreSlug == null ? "" : genreSlug.trim().toLowerCase());
        if (slug.startsWith("truyen-")) {
            slug = slug.substring("truyen-".length());
        }

        final String wantedSlug = slug;
        Genre genre = genreRepository.findAll().stream()
                .filter(g -> SlugUtil.slugify(g.getName()).equals(wantedSlug))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Genre not found"));

        // Reuse existing /api/truyen filtering by genreId.
        return getAll(page, size, sortBy, sortDir, null, null, genre.getId(), null, null);
    }

    @GetMapping("/{id}")
    public Truyen getById(@PathVariable Long id) {
        Truyen truyen = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
        requireVisible(truyen);
        return truyen;
    }

    @GetMapping("/{id}/genres")
    public List<Genre> getGenresByTruyenId(@PathVariable Long id) {
        Truyen truyen = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
        requireVisible(truyen);

        List<Long> ids = truyenGenreRepository.findByIdTruyenId(id).stream()
                .map(tg -> tg.getId() == null ? null : tg.getId().getGenreId())
                .filter(gid -> gid != null)
                .toList();
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<Long, Genre> byId = new LinkedHashMap<>();
        for (Genre g : genreRepository.findAllById(ids)) {
            byId.put(g.getId(), g);
        }

        List<Genre> ordered = new ArrayList<>(ids.size());
        for (Long gid : ids) {
            Genre g = byId.get(gid);
            if (g != null) {
                ordered.add(g);
            }
        }
        return ordered;
    }

    @GetMapping("/slug/{slug}")
    public Truyen getBySlug(@PathVariable String slug) {
        Truyen truyen = truyenRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
        requireVisible(truyen);
        return truyen;
    }

    @GetMapping("/slug/{slug}/genres")
    public List<Genre> getGenresBySlug(@PathVariable String slug) {
        Truyen truyen = truyenRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
        requireVisible(truyen);
        return getGenresByTruyenId(truyen.getId());
    }

    @GetMapping("/{id}/status")
    public Map<String, Object> getStatus(@PathVariable Long id) {
        Truyen truyen = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
        requireVisible(truyen);

        return Map.of(
                "id", truyen.getId(),
                "title", truyen.getTitle(),
                "status", truyen.getStatus(),
                "publishStatus", truyen.getPublishStatus(),
                "approvedAt", truyen.getApprovedAt()
        );
    }

    @GetMapping("/{id}/stats")
    public Map<String, Object> getStats(@PathVariable Long id) {
        Truyen truyen = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
        requireVisible(truyen);

        long chapters = chapterRepository.countByTruyenId(id);
        long comments = commentRepository.countByTruyenId(id);
        long favorites = favoriteRepository.countByIdTruyenId(id);
        Double avgRating = ratingRepository.averageRating(id);

        return Map.of(
                "truyenId", id,
                "title", truyen.getTitle(),
                "views", truyen.getViewCount() == null ? 0L : truyen.getViewCount(),
                "chapters", chapters,
                "comments", comments,
                "favorites", favorites,
                "averageRating", avgRating == null ? 0.0 : avgRating
        );
    }

    @GetMapping("/{id}/chapters")
    public Page<Chapter> getChaptersByTruyen(@PathVariable Long id,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String sort,
                                             @RequestParam(defaultValue = "asc") String sortDir) {
        Truyen truyen = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
        requireVisible(truyen);
        // Compatibility endpoint for FE routes like: /api/truyen/{id}/chapters?sort=chapterNumber&page=0&size=200
        String sortBy = (sort == null || sort.isBlank()) ? "chapterNumber" : sort;
        Sort s = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, s);
        return chapterRepository.findByTruyenId(id, pageable);
    }

    @PostMapping
    @Transactional
    public Truyen create(@Valid @RequestBody CreateTruyenRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Set<Long> uniqueGenreIds = null;
        if (request.getGenreIds() != null) {
            uniqueGenreIds = new LinkedHashSet<>();
            for (Long gid : request.getGenreIds()) {
                if (gid == null) {
                    throw new ResponseStatusException(BAD_REQUEST, "genreIds must not contain null");
                }
                uniqueGenreIds.add(gid);
            }
            if (!uniqueGenreIds.isEmpty()) {
                long existingGenres = genreRepository.countByIdIn(uniqueGenreIds);
                if (existingGenres != uniqueGenreIds.size()) {
                    throw new ResponseStatusException(BAD_REQUEST, "One or more genres not found");
                }
            }
        }

        Truyen truyen = new Truyen();
        truyen.setId(null);
        truyen.setTitle(request.getTitle());
        truyen.setSlug(request.getSlug());
        truyen.setDescription(request.getDescription());
        truyen.setCoverImage(request.getCoverImage());
        truyen.setAuthorId(currentUser.getId());
        truyen.setAuthorName(resolveAuthorName(request.getAuthorName(), currentUser.getUsername()));
        truyen.setStatus(StoryStatus.PENDING);
        truyen.setApprovedAt(null);
        truyen.setPublishStatus(request.getPublishStatus() == null ? PublishStatus.ONGOING : request.getPublishStatus());
        truyen.setViewCount(0L);

        Truyen saved = truyenRepository.save(truyen);

        if (uniqueGenreIds != null && !uniqueGenreIds.isEmpty()) {
            List<TruyenGenre> links = new ArrayList<>(uniqueGenreIds.size());
            for (Long gid : uniqueGenreIds) {
                TruyenGenre link = new TruyenGenre();
                link.setId(new TruyenGenreId(saved.getId(), gid));
                links.add(link);
            }
            truyenGenreRepository.saveAll(links);
        }

        return saved;
    }

    @PutMapping("/{id}")
    public Truyen update(@PathVariable Long id, @Valid @RequestBody UpdateTruyenRequest payload) {
        User currentUser = currentUserService.getCurrentUser();

        Truyen existing = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));

        boolean isOwner = existing.getAuthorId() != null && existing.getAuthorId().equals(currentUser.getId());
        boolean isAdmin = currentUserService.isAdmin(currentUser);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(FORBIDDEN, "You cannot edit this story");
        }

        existing.setTitle(payload.getTitle());
        existing.setSlug(payload.getSlug());
        existing.setDescription(payload.getDescription());
        existing.setCoverImage(payload.getCoverImage());
        if (payload.getAuthorName() != null) {
            existing.setAuthorName(resolveAuthorName(payload.getAuthorName(), currentUser.getUsername()));
        }
        if (payload.getPublishStatus() != null) {
            existing.setPublishStatus(payload.getPublishStatus());
        }

        if (!isAdmin) {
            existing.setStatus(StoryStatus.PENDING);
            existing.setApprovedAt(null);
        }

        return truyenRepository.save(existing);
    }

    @PatchMapping("/{id}/details")
    @Transactional
    public Map<String, Object> updateDetails(@PathVariable Long id, @Valid @RequestBody UpdateTruyenDetailsRequest payload) {
        User currentUser = currentUserService.getCurrentUser();

        Truyen existing = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));

        boolean isOwner = existing.getAuthorId() != null && existing.getAuthorId().equals(currentUser.getId());
        boolean isAdmin = currentUserService.isAdmin(currentUser);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(FORBIDDEN, "You cannot edit this story");
        }

        boolean changed = false;

        if (payload.getTitle() != null) {
            if (payload.getTitle().isBlank()) {
                throw new ResponseStatusException(BAD_REQUEST, "title must not be blank");
            }
            existing.setTitle(payload.getTitle().trim());
            changed = true;
        }

        if (payload.getSlug() != null) {
            String slug = payload.getSlug().trim();
            existing.setSlug(slug.isBlank() ? null : slug);
            changed = true;
        }

        if (payload.getDescription() != null) {
            existing.setDescription(payload.getDescription());
            changed = true;
        }

        if (payload.getCoverImage() != null) {
            String cover = payload.getCoverImage().trim();
            existing.setCoverImage(cover.isBlank() ? null : cover);
            changed = true;
        }

        if (payload.getAuthorName() != null) {
            existing.setAuthorName(resolveAuthorName(payload.getAuthorName(), currentUser.getUsername()));
            changed = true;
        }

        if (payload.getPublishStatus() != null) {
            existing.setPublishStatus(payload.getPublishStatus());
            changed = true;
        }

        if (payload.getGenreIds() != null) {
            Set<Long> uniqueIds = new LinkedHashSet<>();
            for (Long gid : payload.getGenreIds()) {
                if (gid == null) {
                    throw new ResponseStatusException(BAD_REQUEST, "genreIds must not contain null");
                }
                uniqueIds.add(gid);
            }

            if (!uniqueIds.isEmpty()) {
                long existingGenres = genreRepository.countByIdIn(uniqueIds);
                if (existingGenres != uniqueIds.size()) {
                    throw new ResponseStatusException(BAD_REQUEST, "One or more genres not found");
                }
            }

            truyenGenreRepository.deleteByIdTruyenId(id);
            if (!uniqueIds.isEmpty()) {
                List<TruyenGenre> links = new ArrayList<>(uniqueIds.size());
                for (Long gid : uniqueIds) {
                    TruyenGenre link = new TruyenGenre();
                    link.setId(new TruyenGenreId(id, gid));
                    links.add(link);
                }
                truyenGenreRepository.saveAll(links);
            }
            changed = true;
        }

        if (changed && !isAdmin) {
            existing.setStatus(StoryStatus.PENDING);
            existing.setApprovedAt(null);
        }

        Truyen saved = truyenRepository.save(existing);
        List<TruyenGenre> genres = truyenGenreRepository.findByIdTruyenId(id);
        List<Long> genreIds = genres.stream()
                .map(g -> g.getId() == null ? null : g.getId().getGenreId())
                .filter(gid -> gid != null)
                .toList();

        Map<Long, Genre> byId = new LinkedHashMap<>();
        if (!genreIds.isEmpty()) {
            for (Genre g : genreRepository.findAllById(genreIds)) {
                byId.put(g.getId(), g);
            }
        }
        List<Genre> orderedGenres = new ArrayList<>(genreIds.size());
        for (Long gid : genreIds) {
            Genre g = byId.get(gid);
            if (g != null) {
                orderedGenres.add(g);
            }
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("truyen", saved);
        resp.put("genreIds", genreIds);
        resp.put("genres", orderedGenres);
        return resp;
    }

    private String resolveAuthorName(String requestedAuthorName, String fallbackUsername) {
        if (requestedAuthorName == null) {
            return fallbackUsername;
        }
        String trimmed = requestedAuthorName.trim();
        return trimmed.isEmpty() ? fallbackUsername : trimmed;
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
            // Hide existence of unapproved stories from anonymous callers.
            throw new ResponseStatusException(NOT_FOUND, "Truyen not found");
        }
        if (currentUserService.isAdmin(current)) {
            return;
        }

        throw new ResponseStatusException(NOT_FOUND, "Truyen not found");
    }

    @PostMapping("/{id}/increment-view")
    public Truyen incrementView(@PathVariable Long id) {
        Truyen truyen = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));
        requireVisible(truyen);
        long current = truyen.getViewCount() == null ? 0 : truyen.getViewCount();
        truyen.setViewCount(current + 1);
        return truyenRepository.save(truyen);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable Long id) {
        User currentUser = currentUserService.getCurrentUser();

        Truyen existing = truyenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Truyen not found"));

        boolean isOwner = existing.getAuthorId() != null && existing.getAuthorId().equals(currentUser.getId());
        boolean isAdmin = currentUserService.isAdmin(currentUser);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(FORBIDDEN, "You cannot delete this story");
        }

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
        truyenRepository.delete(existing);
    }
}
