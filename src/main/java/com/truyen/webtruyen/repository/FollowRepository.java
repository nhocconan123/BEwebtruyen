package com.truyen.webtruyen.repository;

import com.truyen.webtruyen.entity.Follow;
import com.truyen.webtruyen.entity.id.FollowId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    List<Follow> findByIdFollowerId(Long followerId);
    List<Follow> findByIdFollowingId(Long followingId);
    Page<Follow> findByIdFollowerId(Long followerId, Pageable pageable);
    Page<Follow> findByIdFollowingId(Long followingId, Pageable pageable);
}
