package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.dto.rating.RateTruyenRequest;
import com.truyen.webtruyen.dto.rating.RatingSummaryResponse;
import com.truyen.webtruyen.entity.Rating;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.id.RatingId;
import com.truyen.webtruyen.repository.RatingRepository;
import com.truyen.webtruyen.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingRepository ratingRepository;
    private final CurrentUserService currentUserService;

    public RatingController(RatingRepository ratingRepository, CurrentUserService currentUserService) {
        this.ratingRepository = ratingRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/truyen/{truyenId}")
    public List<Rating> getByTruyen(@PathVariable Long truyenId) {
        return ratingRepository.findByIdTruyenId(truyenId);
    }

    @GetMapping("/truyen/{truyenId}/average")
    public Map<String, Object> getAverage(@PathVariable Long truyenId) {
        Double average = ratingRepository.averageRating(truyenId);
        long count = ratingRepository.countByIdTruyenId(truyenId);
        Integer rounded = (average == null || count == 0) ? null : (int) Math.ceil(average);
        return Map.of(
                "truyenId", truyenId,
                "average", average == null ? 0.0 : average,
                "averageRounded", rounded == null ? 0 : rounded,
                "count", count
        );
    }

    @GetMapping("/truyen/{truyenId}/summary")
    public RatingSummaryResponse getSummary(@PathVariable Long truyenId) {
        long count = ratingRepository.countByIdTruyenId(truyenId);
        Double average = count == 0 ? null : ratingRepository.averageRating(truyenId);
        Integer rounded = (average == null) ? null : (int) Math.ceil(average);

        User current = currentUserService.getCurrentUserOrNull();
        Integer myRating = null;
        if (current != null) {
            myRating = ratingRepository.findById(new RatingId(current.getId(), truyenId))
                    .map(Rating::getRating)
                    .orElse(null);
        }

        RatingSummaryResponse resp = new RatingSummaryResponse();
        resp.setTruyenId(truyenId);
        resp.setCount(count);
        resp.setAverage(average);
        resp.setAverageRounded(rounded);
        resp.setMyRating(myRating);
        return resp;
    }

    @PostMapping("/truyen/{truyenId}")
    public RatingSummaryResponse createOrUpdate(@PathVariable Long truyenId, @Valid @RequestBody RateTruyenRequest payload) {
        User current = currentUserService.getCurrentUser();
        Integer ratingValue = payload.getRatingValue();

        Rating rating = new Rating();
        rating.setId(new RatingId(current.getId(), truyenId));
        rating.setRating(ratingValue);
        ratingRepository.save(rating);

        return getSummary(truyenId);
    }

    @DeleteMapping("/truyen/{truyenId}")
    public RatingSummaryResponse deleteMine(@PathVariable Long truyenId) {
        User current = currentUserService.getCurrentUser();
        ratingRepository.deleteById(new RatingId(current.getId(), truyenId));
        return getSummary(truyenId);
    }
}
