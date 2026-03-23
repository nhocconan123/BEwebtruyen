package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.entity.ReadingHistory;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.id.ReadingHistoryId;
import com.truyen.webtruyen.repository.ReadingHistoryRepository;
import com.truyen.webtruyen.service.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reading-history")
public class ReadingHistoryController {

    private final ReadingHistoryRepository readingHistoryRepository;
    private final CurrentUserService currentUserService;

    public ReadingHistoryController(ReadingHistoryRepository readingHistoryRepository,
                                    CurrentUserService currentUserService) {
        this.readingHistoryRepository = readingHistoryRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public Page<ReadingHistory> getByUser(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        User currentUser = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastReadAt").descending());
        return readingHistoryRepository.findByIdUserIdOrderByLastReadAtDesc(currentUser.getId(), pageable);
    }

    @PostMapping("/{chapterId}")
    public ReadingHistory createOrUpdate(@PathVariable Long chapterId) {
        User currentUser = currentUserService.getCurrentUser();
        ReadingHistory item = new ReadingHistory();
        item.setId(new ReadingHistoryId(currentUser.getId(), chapterId));
        item.setLastReadAt(LocalDateTime.now());
        return readingHistoryRepository.save(item);
    }

    @DeleteMapping("/{chapterId}")
    public void delete(@PathVariable Long chapterId) {
        User currentUser = currentUserService.getCurrentUser();
        readingHistoryRepository.deleteById(new ReadingHistoryId(currentUser.getId(), chapterId));
    }
}
