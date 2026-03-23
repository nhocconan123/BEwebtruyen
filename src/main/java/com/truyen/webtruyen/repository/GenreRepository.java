package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    boolean existsByNameIgnoreCase(String name);

    long countByIdIn(Collection<Long> ids);
}
