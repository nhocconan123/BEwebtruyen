package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.TruyenGenre;
import com.truyen.webtruyen.entity.id.TruyenGenreId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruyenGenreRepository extends JpaRepository<TruyenGenre, TruyenGenreId> {
    List<TruyenGenre> findByIdTruyenId(Long truyenId);

    void deleteByIdTruyenId(Long truyenId);
}
