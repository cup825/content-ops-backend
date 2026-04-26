package com.bytedance.content.content;

import com.bytedance.content.admin.entity.User;
import com.bytedance.content.admin.repository.UserRepository;
import com.bytedance.content.admin.service.PermissionCheckService;
import com.bytedance.content.common.enums.ContentStatus;
import com.bytedance.content.common.exception.BusinessException;
import com.bytedance.content.content.dto.CreateContentRequest;
import com.bytedance.content.content.dto.CreateContentResponse;
import com.bytedance.content.content.dto.UpdateContentRequest;
import com.bytedance.content.content.entity.Content;
import com.bytedance.content.content.repository.ContentRepository;
import com.bytedance.content.content.repository.OperationLogRepository;
import com.bytedance.content.content.service.ContentService;
import com.bytedance.content.content.service.OperationLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ContentService 单元测试
 *
 * 使用 Mockito 模拟所有依赖（Repository、Service），
 * 只测试 ContentService 自身的业务逻辑，不需要启动 Spring 容器或连接数据库。
 *
 * @Mock     → 创建一个假对象，默认所有方法返回 null/0/false
 * @InjectMocks → 创建真实的 ContentService，并把上面的假对象注入进去
 * when(...).thenReturn(...) → 指定假对象某个方法被调用时返回什么值
 */
@ExtendWith(MockitoExtension.class)
public class ContentServiceTest {

    @Mock private ContentRepository contentRepository;
    @Mock private UserRepository userRepository;
    @Mock private PermissionCheckService permissionService;
    @Mock private OperationLogService operationLogService;
    @Mock private OperationLogRepository operationLogRepository;

    @InjectMocks
    private ContentService contentService;

    // ---- 测试1：正常创建内容，返回草稿状态 ----
    @Test
    public void createContent_success_returnsDraft() {
        User mockUser = new User();
        mockUser.setId(2L);
        mockUser.setUsername("operator1");

        Content savedContent = new Content();
        savedContent.setId(1L);
        savedContent.setStatus(ContentStatus.DRAFT);

        when(permissionService.canCreateContent(2L)).thenReturn(true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockUser));
        when(contentRepository.save(any())).thenReturn(savedContent);

        CreateContentRequest request = new CreateContentRequest();
        request.setCreatorId(2L);
        request.setTitle("测试标题");
        request.setContent("测试内容");

        CreateContentResponse response = contentService.createContent(request);

        assertNotNull(response);
        assertEquals(1L, response.getContentId());
        assertEquals(ContentStatus.DRAFT, response.getStatus());
        // 验证操作日志被记录了一次
        verify(operationLogService, times(1)).log(2L, "CREATE_CONTENT", 1L);
    }

    // ---- 测试2：没有权限的用户创建内容，应抛出403 ----
    @Test
    public void createContent_noPermission_throws403() {
        when(permissionService.canCreateContent(3L)).thenReturn(false);

        CreateContentRequest request = new CreateContentRequest();
        request.setCreatorId(3L);
        request.setTitle("标题");
        request.setContent("内容");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> contentService.createContent(request));

        assertEquals(403, ex.getCode());
        // 权限不足时，不应该调用 save
        verify(contentRepository, never()).save(any());
    }

    // ---- 测试3：非草稿状态的内容不能编辑，应抛出400 ----
    @Test
    public void updateContent_nonDraftStatus_throws400() {
        Content content = new Content();
        content.setId(1L);
        content.setStatus(ContentStatus.PENDING); // pending 不能编辑
        User creator = new User();
        creator.setId(2L);
        content.setCreator(creator);

        when(contentRepository.findById(1L)).thenReturn(Optional.of(content));
        when(permissionService.canEditContent(2L, 2L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> contentService.updateContent(1L, 2L, new UpdateContentRequest()));

        assertEquals(400, ex.getCode());
    }

    // ---- 测试4：草稿状态可以提交审核，状态变为 PENDING ----
    @Test
    public void submitForReview_fromDraft_statusBecomePending() {
        Content content = new Content();
        content.setId(1L);
        content.setStatus(ContentStatus.DRAFT);
        User creator = new User();
        creator.setId(2L);
        content.setCreator(creator);

        Content savedContent = new Content();
        savedContent.setId(1L);
        savedContent.setStatus(ContentStatus.PENDING);

        when(contentRepository.findById(1L)).thenReturn(Optional.of(content));
        when(permissionService.isContentCreator(2L, 2L)).thenReturn(true);
        when(contentRepository.save(any())).thenReturn(savedContent);

        var response = contentService.submitForReview(1L, 2L);

        assertEquals("PENDING", response.getStatus());
        assertEquals("提交审核成功", response.getMessage());
    }

    // ---- 测试5：查询不存在的内容时应抛出404 ----
    @Test
    public void deleteContent_contentNotFound_throws404() {
        when(contentRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> contentService.deleteContent(99L, 2L));

        assertEquals(404, ex.getCode());
    }
}

