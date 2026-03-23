package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByTruyenIdOrderByChapterNumberAsc(Long truyenId);
    Page<Chapter> findByTruyenId(Long truyenId, Pageable pageable);
    long countByTruyenId(Long truyenId);

    Optional<Chapter> findByTruyenIdAndChapterNumber(Long truyenId, Integer chapterNumber);

    Optional<Chapter> findFirstByTruyenIdAndChapterNumberLessThanOrderByChapterNumberDesc(Long truyenId, Integer chapterNumber);

    Optional<Chapter> findFirstByTruyenIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(Long truyenId, Integer chapterNumber);

    void deleteByTruyenId(Long truyenId);
}
