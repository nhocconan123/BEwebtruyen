package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.Comment;
import com.truyen.webtruyen.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {

    private final CommentRepository commentRepository;

    public AdminCommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping
    public Page<Comment> getAll(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) Long truyenId,
                                @RequestParam(required = false) Long chapterId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (chapterId != null) {
            return commentRepository.findByChapterId(chapterId, pageable);
        }
        if (truyenId != null) {
            return commentRepository.findByTruyenId(truyenId, pageable);
        }
        return commentRepository.findAll(pageable);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Comment not found");
        }
        commentRepository.deleteById(id);
    }
}

