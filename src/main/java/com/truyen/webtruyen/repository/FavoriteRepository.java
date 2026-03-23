package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.Favorite;
import com.truyen.webtruyen.entity.id.FavoriteId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    List<Favorite> findByIdUserId(Long userId);
    Page<Favorite> findByIdUserId(Long userId, Pageable pageable);
    long countByIdTruyenId(Long truyenId);

    void deleteByIdTruyenId(Long truyenId);
}
