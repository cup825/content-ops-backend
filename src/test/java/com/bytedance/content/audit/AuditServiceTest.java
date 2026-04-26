package com.bytedance.content.audit;

import com.bytedance.content.admin.entity.User;
import com.bytedance.content.admin.repository.UserRepository;
import com.bytedance.content.admin.service.PermissionCheckService;
import com.bytedance.content.audit.dto.AuditRequest;
import com.bytedance.content.audit.dto.AuditResponse;
import com.bytedance.content.audit.entity.AuditRecord;
import com.bytedance.content.audit.repository.AuditRecordRepository;
import com.bytedance.content.audit.service.AuditService;
import com.bytedance.content.common.enums.AuditStatus;
import com.bytedance.content.common.enums.ContentStatus;
import com.bytedance.content.common.exception.BusinessException;
import com.bytedance.content.content.entity.Content;
import com.bytedance.content.content.repository.ContentRepository;
import com.bytedance.content.content.service.OperationLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * AuditService 单元测试
 * 测试审核流程中的状态流转、业务规则（驳回必填原因、只能审核 PENDING）
 */
@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

    @Mock private ContentRepository contentRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuditRecordRepository auditRecordRepository;
    @Mock private PermissionCheckService permissionService;
    @Mock private OperationLogService operationLogService;

    @InjectMocks
    private AuditService auditService;

    // ---- 测试1：审核通过，内容状态变为 APPROVED ----
    @Test
    public void auditContent_approve_statusBecomesApproved() {
        User reviewer = new User(); reviewer.setId(3L); reviewer.setUsername("reviewer1");
        Content content = new Content(); content.setId(1L); content.setStatus(ContentStatus.PENDING);
        Content savedContent = new Content(); savedContent.setId(1L); savedContent.setStatus(ContentStatus.APPROVED);
        AuditRecord savedRecord = new AuditRecord(); savedRecord.setId(10L); savedRecord.setStatus(AuditStatus.APPROVED);

        when(permissionService.canAuditContent(3L)).thenReturn(true);
        when(userRepository.findById(3L)).thenReturn(Optional.of(reviewer));
        when(contentRepository.findById(1L)).thenReturn(Optional.of(content));
        when(contentRepository.save(any())).thenReturn(savedContent);
        when(auditRecordRepository.save(any())).thenReturn(savedRecord);

        AuditRequest request = new AuditRequest();
        request.setReviewerId(3L);
        request.setContentId(1L);
        request.setAction("APPROVED");

        AuditResponse response = auditService.auditContent(request);

        assertEquals("APPROVED", response.getStatus());
    }

    // ---- 测试2：拒绝审核时不填驳回原因，应抛出400 ----
    @Test
    public void auditContent_rejectWithoutComment_throws400() {
        User reviewer = new User(); reviewer.setId(3L);
        Content content = new Content(); content.setId(1L); content.setStatus(ContentStatus.PENDING);

        when(permissionService.canAuditContent(3L)).thenReturn(true);
        when(userRepository.findById(3L)).thenReturn(Optional.of(reviewer));
        when(contentRepository.findById(1L)).thenReturn(Optional.of(content));

        AuditRequest request = new AuditRequest();
        request.setReviewerId(3L);
        request.setContentId(1L);
        request.setAction("REJECTED");
        request.setComment(""); // 空评论

        BusinessException ex = assertThrows(BusinessException.class,
                () -> auditService.auditContent(request));
        assertEquals(400, ex.getCode());
    }

    // ---- 测试3：只有 PENDING 状态的内容才能被审核 ----
    @Test
    public void auditContent_notPendingStatus_throws400() {
        User reviewer = new User(); reviewer.setId(3L);
        Content content = new Content(); content.setId(1L);
        content.setStatus(ContentStatus.DRAFT); // 草稿不能被审核

        when(permissionService.canAuditContent(3L)).thenReturn(true);
        when(userRepository.findById(3L)).thenReturn(Optional.of(reviewer));
        when(contentRepository.findById(1L)).thenReturn(Optional.of(content));

        AuditRequest request = new AuditRequest();
        request.setReviewerId(3L);
        request.setContentId(1L);
        request.setAction("APPROVED");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> auditService.auditContent(request));
        assertEquals(400, ex.getCode());
    }
}

