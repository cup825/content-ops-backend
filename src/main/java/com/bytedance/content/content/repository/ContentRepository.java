package com.bytedance.content.content.repository;

import com.bytedance.content.common.enums.ContentStatus;
import com.bytedance.content.content.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    // 按状态 + 创建者分页查询
    Page<Content> findByStatusAndCreatorId(ContentStatus status, Long creatorId, Pageable pageable);

    // 只按状态分页查询
    Page<Content> findByStatus(ContentStatus status, Pageable pageable);

    // 只按创建者分页查询
    Page<Content> findByCreatorId(Long creatorId, Pageable pageable);

    @Query("SELECT c FROM Content c WHERE " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:creatorId IS NULL OR c.creator.id = :creatorId)")
    Page<Content> findByConditions(
            @Param("status") ContentStatus status,
            @Param("creatorId") Long creatorId,
            Pageable pageable
    );
}

