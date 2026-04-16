package com.bytedance.content.service.impl;

import com.bytedance.content.common.enums.ContentStatus;
import com.bytedance.content.common.enums.AuditStatus;
import com.bytedance.content.common.exception.BusinessException;
import com.bytedance.content.dto.AuditRequest;
import com.bytedance.content.dto.AuditResponse;
import com.bytedance.content.entity.AuditRecord;
import com.bytedance.content.entity.Content;
import com.bytedance.content.entity.User;
import com.bytedance.content.repository.AuditRecordRepository;
import com.bytedance.content.repository.ContentRepository;
import com.bytedance.content.repository.UserRepository;
import com.bytedance.content.service.AuditService;
import com.bytedance.content.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditRecordRepository auditRecordRepository;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public AuditResponse auditContent(AuditRequest request) {
        // 1. 校验审核人是否存在
        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new BusinessException(404, "审核人不存在"));

        // 2. 校验内容是否存在
        Content content = contentRepository.findById(request.getContentId())
                .orElseThrow(() -> new BusinessException(404, "内容不存在"));

        // 3. 校验内容状态是否为 PENDING（待审核）
        if (content.getStatus() != ContentStatus.PENDING) {
            throw new BusinessException(400, "内容不处于待审核状态，无法审核");
        }

        // 4. 根据审核操作进行状态转换
        AuditStatus auditStatus;
        ContentStatus newContentStatus;

        if ("APPROVED".equals(request.getAction())) {
            // 审核通过：PENDING → APPROVED
            auditStatus = AuditStatus.APPROVED;
            newContentStatus = ContentStatus.APPROVED;
        } else if ("REJECTED".equals(request.getAction())) {
            // 审核拒绝：PENDING → REJECTED
            if (request.getComment() == null || request.getComment().trim().isEmpty()) {
                throw new BusinessException(400, "拒绝内容时，驳回原因必填");
            }
            auditStatus = AuditStatus.REJECTED;
            newContentStatus = ContentStatus.REJECTED;
        } else {
            throw new BusinessException(400, "无效的审核操作，只支持 APPROVED 或 REJECTED");
        }

        // 5. 更新内容状态
        content.setStatus(newContentStatus);
        Content updatedContent = contentRepository.save(content);

        // 6. 创建审核记录
        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setContent(content);
        auditRecord.setReviewer(reviewer);
        auditRecord.setStatus(auditStatus);
        auditRecord.setComment(request.getComment());

        AuditRecord savedAuditRecord = auditRecordRepository.save(auditRecord);

        // 7. 记录操作日志
        String action = "APPROVED".equals(request.getAction()) ? "APPROVE_CONTENT" : "REJECT_CONTENT";
        operationLogService.log(request.getReviewerId(), action, request.getContentId());

        // 8. 返回审核结果
        return new AuditResponse(
                savedAuditRecord.getId(),
                content.getId(),
                newContentStatus.toString(),
                request.getComment()
        );
    }
}

