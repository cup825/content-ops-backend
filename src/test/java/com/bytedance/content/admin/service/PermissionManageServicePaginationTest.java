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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PermissionManageService 分页功能单元测试
 * 
 * 测试范围：
 * 1. 用户分页查询
 * 2. 权限分页查询
 * 3. 排序功能
 * 4. 分页数据验证
 */
@SpringBootTest
@ActiveProfiles("test")
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class
})
public class PermissionManageServicePaginationTest {

    @Autowired
    private PermissionManageService permissionManageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role adminRole;
    private Role operatorRole;

    @BeforeEach
    public void setUp() {
        // 清空用户表
        userRepository.deleteAll();
        // 清空角色表
        roleRepository.deleteAll();

        // 创建并保存角色对象到数据库
        adminRole = new Role();
        adminRole.setRoleName(UserRole.ADMIN);
        adminRole = roleRepository.save(adminRole);

        operatorRole = new Role();
        operatorRole.setRoleName(UserRole.OPERATOR);
        operatorRole = roleRepository.save(operatorRole);

        // 创建测试用户
        createTestUsers();
    }

    private void createTestUsers() {
        // 创建10个测试用户
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword("encoded_password_" + i);
            user.setRole(i == 1 ? adminRole : operatorRole);
            userRepository.save(user);
        }
    }

    @Test
    public void testGetUsersByPage_FirstPage() {
        /**
         * 测试1：分页获取用户 - 第一页
         * 预期：返回第一页的5条用户数据
         */
        PaginationRequest request = new PaginationRequest(1, 5, "id", "asc");
        PaginationResponse<UserResponse> response = permissionManageService.getUsersByPage(request);

        // 验证响应数据
        assertNotNull(response, "响应不能为空");
        assertEquals(5, response.getContent().size(), "第一页应该返回5条数据");
        assertEquals(10, response.getTotal(), "数据库中应该有10个用户");
        assertEquals(1, response.getPage(), "当前页码应该是1");
        assertEquals(5, response.getPageSize(), "每页条数应该是5");
        assertEquals(2, response.getTotalPages(), "总共应该有2页");

        // 验证第一条数据
        UserResponse firstUser = response.getContent().get(0);
        assertEquals(1L, firstUser.getId(), "第一条数据ID应该是1");
        assertEquals("user1", firstUser.getUsername(), "用户名应该是user1");
    }

    @Test
    public void testGetUsersByPage_SecondPage() {
        /**
         * 测试2：分页获取用户 - 第二页
         * 预期：返回第二页的5条用户数据
         */
        PaginationRequest request = new PaginationRequest(2, 5, "id", "asc");
        PaginationResponse<UserResponse> response = permissionManageService.getUsersByPage(request);

        // 验证响应数据
        assertNotNull(response, "响应不能为空");
        assertEquals(5, response.getContent().size(), "第二页应该返回5条数据");
        assertEquals(10, response.getTotal(), "总用户数应该是10");
        assertEquals(2, response.getPage(), "当前页码应该是2");
        assertEquals(2, response.getTotalPages(), "总页数应该是2");

        // 验证第二页的第一条数据
        UserResponse firstUserOnPage2 = response.getContent().get(0);
        assertEquals(6L, firstUserOnPage2.getId(), "第二页的第一条数据ID应该是6");
        assertEquals("user6", firstUserOnPage2.getUsername(), "用户名应该是user6");
    }

    @Test
    public void testGetUsersByPage_WithSorting() {
        /**
         * 测试3：分页查询 + 排序（倒序）
         * 预期：用户按ID倒序排列，最大ID在前
         */
        PaginationRequest request = new PaginationRequest(1, 5, "id", "desc");
        PaginationResponse<UserResponse> response = permissionManageService.getUsersByPage(request);

        // 验证数据按倒序排列
        List<UserResponse> content = response.getContent();
        assertEquals(10L, content.get(0).getId(), "倒序后第一个应该是ID最大的用户（10）");
        assertEquals(6L, content.get(4).getId(), "倒序后第五个应该是ID为6的用户");
    }

    @Test
    public void testGetUsersByPage_DefaultPageSize() {
        /**
         * 测试4：未指定pageSize时，使用默认值（10）
         * 预期：返回10条数据（全部用户）
         */
        PaginationRequest request = new PaginationRequest(1, null, "id", "asc");
        PaginationResponse<UserResponse> response = permissionManageService.getUsersByPage(request);

        // 验证使用默认pageSize
        assertEquals(10, response.getContent().size(), "未指定pageSize时应该返回默认的10条");
        assertEquals(1, response.getTotalPages(), "所有数据都在第一页");
    }

    @Test
    public void testGetUsersByPage_PageOutOfRange() {
        /**
         * 测试5：请求超出范围的页码
         * 预期：返回空的content，但totalPages正确
         */
        PaginationRequest request = new PaginationRequest(5, 5, "id", "asc"); // 超出范围
        PaginationResponse<UserResponse> response = permissionManageService.getUsersByPage(request);

        // 验证返回空数据
        assertNotNull(response, "响应不能为空");
        assertEquals(0, response.getContent().size(), "超出范围的页码应该返回空数据");
        assertEquals(10, response.getTotal(), "总数据量不变");
        assertEquals(2, response.getTotalPages(), "总页数应该是2");
    }

    @Test
    public void testPaginationRequest_GetPageNumber() {
        /**
         * 测试6：PaginationRequest 页码转换
         * 用户输入 page=1（从1开始），转换为 pageNumber=0（JPA从0开始）
         */
        PaginationRequest request = new PaginationRequest(1, 10, "id", "asc");
        assertEquals(0, request.getPageNumber(), "页码1应该转换为0");

        PaginationRequest request2 = new PaginationRequest(2, 10, "id", "asc");
        assertEquals(1, request2.getPageNumber(), "页码2应该转换为1");
    }

    @Test
    public void testPaginationRequest_GetPageSize() {
        /**
         * 测试7：PaginationRequest pageSize默认值
         */
        PaginationRequest request1 = new PaginationRequest(1, null, "id", "asc");
        assertEquals(10, request1.getPageSize(), "pageSize为null时应该默认为10");

        PaginationRequest request2 = new PaginationRequest(1, 20, "id", "asc");
        assertEquals(20, request2.getPageSize(), "pageSize为20时应该返回20");
    }

    @Test
    public void testPaginationRequest_SortOrder() {
        /**
         * 测试8：PaginationRequest 排序方向
         */
        PaginationRequest request1 = new PaginationRequest(1, 10, "id", "desc");
        assertEquals("desc", request1.getSortOrder(), "排序方向为desc");

        PaginationRequest request2 = new PaginationRequest(1, 10, "id", "asc");
        assertEquals("asc", request2.getSortOrder(), "排序方向为asc");

        PaginationRequest request3 = new PaginationRequest(1, 10, "id", null);
        assertEquals("asc", request3.getSortOrder(), "未指定排序方向时默认为asc");

        PaginationRequest request4 = new PaginationRequest(1, 10, "id", "invalid");
        assertEquals("asc", request4.getSortOrder(), "非法排序方向时默认为asc");
    }

    @Test
    public void testGetAllUsers_NoPageination() {
        /**
         * 测试9：获取全量用户列表（不分页）
         * 预期：返回所有用户
         */
        List<UserResponse> users = permissionManageService.getAllUsers();

        assertNotNull(users, "用户列表不能为空");
        assertEquals(10, users.size(), "应该返回所有10个用户");
    }

    @Test
    public void testUserResponse_DataMapping() {
        /**
         * 测试10：用户数据映射检查
         * 验证 Entity 转换为 Response DTO 的数据完整性
         */
        PaginationRequest request = new PaginationRequest(1, 1, "id", "asc");
        PaginationResponse<UserResponse> response = permissionManageService.getUsersByPage(request);

        UserResponse user = response.getContent().get(0);
        assertNotNull(user.getId(), "用户ID不能为空");
        assertNotNull(user.getUsername(), "用户名不能为空");
        assertNotNull(user.getRoleName(), "角色名不能为空");
        assertNotNull(user.getCreatedAt(), "创建时间不能为空");
    }
}

