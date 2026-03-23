package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.UserGenre;
import com.truyen.webtruyen.entity.id.UserGenreId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGenreRepository extends JpaRepository<UserGenre, UserGenreId> {
    List<UserGenre> findByIdUserId(Long userId);
}
