package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.dto.auth.CreateCommentRequest;
import com.truyen.webtruyen.dto.comment.CommentResponse;
import com.truyen.webtruyen.entity.Comment;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.repository.CommentRepository;
import com.truyen.webtruyen.repository.UserRepository;
import com.truyen.webtruyen.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public CommentController(CommentRepository commentRepository,
                             UserRepository userRepository,
                             CurrentUserService currentUserService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public Page<CommentResponse> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) Long truyenId,
                                        @RequestParam(required = false) Long chapterId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Comment> comments;
        if (chapterId != null) {
            comments = commentRepository.findByChapterId(chapterId, pageable);
        } else if (truyenId != null) {
            comments = commentRepository.findByTruyenId(truyenId, pageable);
        } else {
            comments = commentRepository.findAll(pageable);
        }

        Set<Long> userIds = comments.getContent()
                .stream()
                .map(Comment::getUserId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));

        Map<Long, User> userById = new LinkedHashMap<>();
        if (!userIds.isEmpty()) {
            for (User u : userRepository.findAllById(userIds)) {
                if (u.getId() != null) {
                    userById.put(u.getId(), u);
                }
            }
        }

        return comments.map(c -> toResponse(c, userById.get(c.getUserId())));
    }

    @PostMapping
    public CommentResponse create(@Valid @RequestBody CreateCommentRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Comment comment = new Comment();
        comment.setUserId(currentUser.getId());
        comment.setTruyenId(request.getTruyenId());
        comment.setChapterId(request.getChapterId());
        comment.setContent(request.getContent());
        Comment saved = commentRepository.save(comment);
        return toResponse(saved, currentUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        User currentUser = currentUserService.getCurrentUser();

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Comment not found"));

        boolean isOwner = comment.getUserId() != null && comment.getUserId().equals(currentUser.getId());
        boolean isAdmin = currentUserService.isAdmin(currentUser);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(FORBIDDEN, "You cannot delete this comment");
        }

        commentRepository.deleteById(id);
    }

    private CommentResponse toResponse(Comment comment, User user) {
        CommentResponse resp = new CommentResponse();
        resp.setId(comment.getId());
        resp.setUserId(comment.getUserId());
        resp.setTruyenId(comment.getTruyenId());
        resp.setChapterId(comment.getChapterId());
        resp.setContent(comment.getContent());
        resp.setCreatedAt(comment.getCreatedAt());

        if (user != null) {
            resp.setUsername(user.getUsername());
            resp.setAvatar(user.getAvatar());
        }

        return resp;
    }
}
