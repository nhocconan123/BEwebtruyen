package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.UserGenre;
import com.truyen.webtruyen.entity.id.UserGenreId;
import com.truyen.webtruyen.repository.UserGenreRepository;
import com.truyen.webtruyen.service.CurrentUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-genres")
public class UserGenreController {

    private final UserGenreRepository userGenreRepository;
    private final CurrentUserService currentUserService;

    public UserGenreController(UserGenreRepository userGenreRepository, CurrentUserService currentUserService) {
        this.userGenreRepository = userGenreRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public List<UserGenre> getMine() {
        User currentUser = currentUserService.getCurrentUser();
        return userGenreRepository.findByIdUserId(currentUser.getId());
    }

    @PostMapping("/{genreId}")
    public UserGenre create(@PathVariable Long genreId) {
        User currentUser = currentUserService.getCurrentUser();
        UserGenre userGenre = new UserGenre();
        userGenre.setId(new UserGenreId(currentUser.getId(), genreId));
        return userGenreRepository.save(userGenre);
    }

    @DeleteMapping("/{genreId}")
    public void delete(@PathVariable Long genreId) {
        User currentUser = currentUserService.getCurrentUser();
        userGenreRepository.deleteById(new UserGenreId(currentUser.getId(), genreId));
    }
}
