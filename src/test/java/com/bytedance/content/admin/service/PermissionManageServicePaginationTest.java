package com.bytedance.content.admin.service;

import com.bytedance.content.admin.dto.PaginationRequest;
import com.bytedance.content.admin.dto.PaginationResponse;
import com.bytedance.content.admin.dto.UserResponse;
import com.bytedance.content.admin.entity.Role;
import com.bytedance.content.admin.entity.User;
import com.bytedance.content.admin.repository.RoleRepository;
import com.bytedance.content.admin.repository.UserRepository;
import com.bytedance.content.common.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分页功能集成测试（精简版，只保留3个核心场景）
 */
@SpringBootTest
@ActiveProfiles("test")
public class PermissionManageServicePaginationTest {

    @Autowired
    private PermissionManageService permissionManageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role operatorRole;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        Role role = new Role();
        role.setRoleName(UserRole.OPERATOR);
        role = roleRepository.save(role);
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword("password_" + i);
            user.setRole(role);
            userRepository.save(user);
        }
    }

    @Test
    public void testFirstPage_returnsFiveItems() {
        PaginationResponse<UserResponse> response =
                permissionManageService.getUsersByPage(new PaginationRequest(1, 5, "id", "asc"));
        assertEquals(5, response.getContent().size());
        assertEquals(10, response.getTotal());
        assertEquals(2, response.getTotalPages());
        assertEquals("user1", response.getContent().get(0).getUsername());
    }

    @Test
    public void testSecondPage_returnsNextItems() {
        PaginationResponse<UserResponse> response =
                permissionManageService.getUsersByPage(new PaginationRequest(2, 5, "id", "asc"));
        assertEquals(5, response.getContent().size());
        assertEquals("user6", response.getContent().get(0).getUsername());
    }

    @Test
    public void testOutOfRangePage_returnsEmpty() {
        PaginationResponse<UserResponse> response =
                permissionManageService.getUsersByPage(new PaginationRequest(5, 5, "id", "asc"));
        assertEquals(0, response.getContent().size());
        assertEquals(10, response.getTotal());
    }
}

