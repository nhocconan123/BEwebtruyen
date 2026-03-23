package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTruyenIdOrderByCreatedAtDesc(Long truyenId);
    List<Comment> findByChapterIdOrderByCreatedAtDesc(Long chapterId);
    Page<Comment> findByTruyenId(Long truyenId, Pageable pageable);
    Page<Comment> findByChapterId(Long chapterId, Pageable pageable);
    long countByTruyenId(Long truyenId);

    void deleteByTruyenId(Long truyenId);
}
