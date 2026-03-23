package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.Truyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TruyenRepository extends JpaRepository<Truyen, Long>, JpaSpecificationExecutor<Truyen> {
    @Query("select coalesce(sum(t.viewCount),0) from Truyen t where t.authorId = :authorId")
    long totalViewsByAuthorId(@Param("authorId") Long authorId);

    Optional<Truyen> findBySlug(String slug);
}
