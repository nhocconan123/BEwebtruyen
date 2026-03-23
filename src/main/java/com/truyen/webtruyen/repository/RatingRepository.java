package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.Rating;
import com.truyen.webtruyen.entity.id.RatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    List<Rating> findByIdTruyenId(Long truyenId);
    long countByIdTruyenId(Long truyenId);

    @Query("select avg(r.rating) from Rating r where r.id.truyenId = :truyenId")
    Double averageRating(@Param("truyenId") Long truyenId);

    void deleteByIdTruyenId(Long truyenId);
}
