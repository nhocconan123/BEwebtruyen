package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.ReadingHistory;
import com.truyen.webtruyen.entity.id.ReadingHistoryId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, ReadingHistoryId> {
    List<ReadingHistory> findByIdUserIdOrderByLastReadAtDesc(Long userId);
    Page<ReadingHistory> findByIdUserIdOrderByLastReadAtDesc(Long userId, Pageable pageable);

    void deleteByIdChapterIdIn(Collection<Long> chapterIds);
}
