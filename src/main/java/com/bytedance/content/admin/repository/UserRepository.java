package com.bytedance.content.admin.repository;

import com.bytedance.content.admin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    /**
     * 按用户名模糊查询（支持分页）
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.id = CAST(:keyword AS long)")
    Page<User> findByUsernameContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 按角色ID查询（支持分页）
     */
    Page<User> findByRoleId(Long roleId, Pageable pageable);
}

