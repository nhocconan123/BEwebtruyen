package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.enums.UserStatus;
import com.truyen.webtruyen.repository.TruyenRepository;
import com.truyen.webtruyen.repository.UserRepository;
import com.truyen.webtruyen.service.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final TruyenRepository truyenRepository;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          CurrentUserService currentUserService,
                          TruyenRepository truyenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
        this.truyenRepository = truyenRepository;
    }

    @GetMapping
    public Page<User> getAll(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable);
    }

    @GetMapping("/me")
    public Map<String, Object> me() {
        User currentUser = currentUserService.getCurrentUser();
        long totalViews = truyenRepository.totalViewsByAuthorId(currentUser.getId());

        return Map.of(
                "id", currentUser.getId(),
                "username", currentUser.getUsername(),
                "email", currentUser.getEmail(),
                "avatar", currentUser.getAvatar() == null ? "" : currentUser.getAvatar(),
                "bio", currentUser.getBio() == null ? "" : currentUser.getBio(),
                "role", currentUser.getRole(),
                "status", currentUser.getStatus(),
                "totalViews", totalViews
        );
    }

    @GetMapping("/suggestions")
    public List<User> suggestions(@RequestParam(defaultValue = "10") int size) {
        User currentUser = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(0, size);
        return userRepository.findUsersWithCommonGenres(currentUser.getId(), pageable);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    @PostMapping
    public User create(@RequestBody User user) {
        user.setId(null);
        if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "User not found");
        }
        user.setId(id);
        if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
    }
}
