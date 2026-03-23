package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.Favorite;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.id.FavoriteId;
import com.truyen.webtruyen.repository.FavoriteRepository;
import com.truyen.webtruyen.service.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final CurrentUserService currentUserService;

    public FavoriteController(FavoriteRepository favoriteRepository, CurrentUserService currentUserService) {
        this.favoriteRepository = favoriteRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public Page<Favorite> getMyFavorites(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        User currentUser = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return favoriteRepository.findByIdUserId(currentUser.getId(), pageable);
    }

    @PostMapping("/{truyenId}")
    public Favorite create(@PathVariable Long truyenId) {
        User currentUser = currentUserService.getCurrentUser();
        Favorite favorite = new Favorite();
        favorite.setId(new FavoriteId(currentUser.getId(), truyenId));
        return favoriteRepository.save(favorite);
    }

    @DeleteMapping("/{truyenId}")
    public void delete(@PathVariable Long truyenId) {
        User currentUser = currentUserService.getCurrentUser();
        favoriteRepository.deleteById(new FavoriteId(currentUser.getId(), truyenId));
    }
}
