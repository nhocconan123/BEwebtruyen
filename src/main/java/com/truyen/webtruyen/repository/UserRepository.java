package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("""
            select u from User u
            join UserGenre ug on ug.id.userId = u.id
            where u.id <> :userId
              and ug.id.genreId in (select g.id.genreId from UserGenre g where g.id.userId = :userId)
            group by u.id
            order by count(ug.id.genreId) desc
            """)
    List<User> findUsersWithCommonGenres(@Param("userId") Long userId, Pageable pageable);
}
