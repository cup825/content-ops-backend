package com.bytedance.content.admin.controller;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PermissionController 分页API集成测试
 * 
 * 测试范围：
 * 1. 用户分页接口 GET /api/admin/users/page
 * 2. 权限分页接口 GET /api/admin/permissions/page
 * 3. 角色查询接口 GET /api/admin/roles/{roleId}
 * 4. HTTP 状态码和响应格式
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PermissionControllerPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role adminRole;
    private Role operatorRole;

    @BeforeEach
    public void setUp() {
        // 清空数据
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // 创建角色
        adminRole = new Role();
        adminRole.setRoleName(UserRole.ADMIN);
        adminRole = roleRepository.save(adminRole);

        operatorRole = new Role();
        operatorRole.setRoleName(UserRole.OPERATOR);
        operatorRole = roleRepository.save(operatorRole);

        // 创建测试用户
        for (int i = 1; i <= 8; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword("encoded_password_" + i);
            user.setRole(i == 1 ? adminRole : operatorRole);
            userRepository.save(user);
        }
    }

    @Test
    public void testGetUsersByPage_Success() throws Exception {
        /**
         * 测试1：GET /api/admin/users/page - 成功获取用户分页
         * 预期：返回 200 OK，包含分页数据
         */
        MvcResult result = mockMvc.perform(
                get("/api/admin/users/page")
                        .param("page", "1")
                        .param("pageSize", "5")
        )
        .andExpect(status().isOk())
        .andReturn();

        // 解析响应
        String content = result.getResponse().getContentAsString();
        assertNotNull(content, "响应体不能为空");
        assertTrue(content.contains("\"content\""), "响应应该包含 content 字段");
        assertTrue(content.contains("\"total\""), "响应应该包含 total 字段");
        assertTrue(content.contains("\"page\""), "响应应该包含 page 字段");
        assertTrue(content.contains("\"totalPages\""), "响应应该包含 totalPages 字段");
    }

    @Test
    public void testGetUsersByPage_WithSorting() throws Exception {
        /**
         * 测试2：GET /api/admin/users/page - 带排序参数
         * 预期：按指定字段和顺序排序
         */
        MvcResult result = mockMvc.perform(
                get("/api/admin/users/page")
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("sortBy", "id")
                        .param("sortOrder", "desc")
        )
        .andExpect(status().isOk())
        .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("\"content\""), "应该返回排序后的数据");
    }

    @Test
    public void testGetUsersByPage_SecondPage() throws Exception {
        /**
         * 测试3：GET /api/admin/users/page - 获取第二页
         * 预期：返回 200 OK，page=2
         */
        MvcResult result = mockMvc.perform(
                get("/api/admin/users/page")
                        .param("page", "2")
                        .param("pageSize", "5")
        )
        .andExpect(status().isOk())
        .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("\"page\":2"), "返回的page应该是2");
    }

    @Test
    public void testGetUsersByPage_DefaultValues() throws Exception {
        /**
         * 测试4：GET /api/admin/users/page - 不带参数时使用默认值
         * 预期：使用默认的 page=1, pageSize=10
         */
        MvcResult result = mockMvc.perform(
                get("/api/admin/users/page")
        )
        .andExpect(status().isOk())
        .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("\"page\":1"), "默认第1页");
        assertTrue(content.contains("\"pageSize\":10"), "默认每页10条");
    }

    @Test
    public void testGetPermissionsByPage_Success() throws Exception {
        /**
         * 测试5：GET /api/admin/permissions/page - 成功获取权限分页
         * 预期：返回 200 OK，权限列表分页
         */
        MvcResult result = mockMvc.perform(
                get("/api/admin/permissions/page")
                        .param("page", "1")
                        .param("pageSize", "10")
        )
        .andExpect(status().isOk())
        .andReturn();

        String content = result.getResponse().getContentAsString();
        assertNotNull(content, "响应体不能为空");
        assertTrue(content.contains("\"content\""), "应该包含权限列表");
    }

    @Test
    public void testGetRoleById_Success() throws Exception {
        /**
         * 测试6：GET /api/admin/roles/{roleId} - 成功获取角色
         * 预期：返回 200 OK，角色信息
         */
        MvcResult result = mockMvc.perform(
                get("/api/admin/roles/" + adminRole.getId())
        )
        .andExpect(status().isOk())
        .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("\"id\":" + adminRole.getId()), "应该包含角色ID");
        assertTrue(content.contains("\"roleName\""), "应该包含角色名");
    }

    @Test
    public void testGetRoleById_NotFound() throws Exception {
        /**
         * 测试7：GET /api/admin/roles/999 - 角色不存在
         * 预期：返回 404 Not Found
         */
        mockMvc.perform(
                get("/api/admin/roles/999")
        )
        .andExpect(status().isNotFound())
        .andReturn();
    }

    @Test
    public void testUpdateRole_NotSupported() throws Exception {
        /**
         * 测试8：PUT /api/admin/roles/{roleId} - 不支持修改
         * 预期：返回 400 Bad Request，提示角色为系统预定义
         */
        String body = "{\"roleName\": \"SUPER_ADMIN\"}";
        MvcResult result = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                        "/api/admin/roles/" + adminRole.getId())
                        .header("X-User-Id", "1")
                        .contentType("application/json")
                        .content(body)
        )
        .andExpect(status().isBadRequest())
        .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("系统预定义"), "应该提示角色为系统预定义");
    }

    @Test
    public void testDeleteRole_NotSupported() throws Exception {
        /**
         * 测试9：DELETE /api/admin/roles/{roleId} - 不支持删除
         * 预期：返回 400 Bad Request
         */
        MvcResult result = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                        "/api/admin/roles/" + adminRole.getId())
                        .header("X-User-Id", "1")
        )
        .andExpect(status().isBadRequest())
        .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("系统预定义"), "应该提示角色为系统预定义");
    }

    @Test
    public void testGetUsersByPage_ResponseFormat() throws Exception {
        /**
         * 测试10：验证响应数据格式完整性
         * 预期：返回的JSON包含所有必需字段
         */
        MvcResult result = mockMvc.perform(
                get("/api/admin/users/page")
                        .param("page", "1")
                        .param("pageSize", "5")
        )
        .andExpect(status().isOk())
        .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        
        // 验证所有必需字段
        String[] requiredFields = {
                "\"content\"",
                "\"total\"",
                "\"page\"",
                "\"pageSize\"",
                "\"totalPages\""
        };

        for (String field : requiredFields) {
            assertTrue(responseBody.contains(field), 
                    "响应格式应该包含字段: " + field);
        }
    }
}

