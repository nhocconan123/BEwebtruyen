package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.Follow;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.id.FollowId;
import com.truyen.webtruyen.repository.FollowRepository;
import com.truyen.webtruyen.service.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowRepository followRepository;
    private final CurrentUserService currentUserService;

    public FollowController(FollowRepository followRepository, CurrentUserService currentUserService) {
        this.followRepository = followRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me/following")
    public Page<Follow> getFollowing(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        User currentUser = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id.followingId").ascending());
        return followRepository.findByIdFollowerId(currentUser.getId(), pageable);
    }

    @GetMapping("/me/followers")
    public Page<Follow> getFollowers(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        User currentUser = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id.followerId").ascending());
        return followRepository.findByIdFollowingId(currentUser.getId(), pageable);
    }

    @PostMapping("/{followingId}")
    public Follow create(@PathVariable Long followingId) {
        User currentUser = currentUserService.getCurrentUser();
        Follow follow = new Follow();
        follow.setId(new FollowId(currentUser.getId(), followingId));
        return followRepository.save(follow);
    }

    @DeleteMapping("/{followingId}")
    public void delete(@PathVariable Long followingId) {
        User currentUser = currentUserService.getCurrentUser();
        followRepository.deleteById(new FollowId(currentUser.getId(), followingId));
    }
}
