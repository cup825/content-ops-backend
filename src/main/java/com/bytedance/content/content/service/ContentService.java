package com.bytedance.content.content.service;

import com.bytedance.content.common.enums.ContentStatus;
import com.bytedance.content.common.exception.BusinessException;
import com.bytedance.content.common.utils.DateTimeUtil;
import com.bytedance.content.common.utils.SecurityUtil;
import com.bytedance.content.content.dto.CreateContentRequest;
import com.bytedance.content.content.dto.CreateContentResponse;
import com.bytedance.content.content.dto.UpdateContentRequest;
import com.bytedance.content.content.entity.Content;
import com.bytedance.content.content.repository.ContentRepository;
import com.bytedance.content.content.repository.OperationLogRepository;
import com.bytedance.content.admin.entity.User;
import com.bytedance.content.admin.repository.UserRepository;
import com.bytedance.content.admin.service.PermissionCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContentService {

    private static final Logger logger = LoggerFactory.getLogger(ContentService.class);

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private PermissionCheckService permissionService;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 从 JWT 安全上下文中获取当前用户 ID，若未登录则抛 401
     */
    private Long requireCurrentUserId() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }

    /**
     * 创建内容，初始状态为 DRAFT
     * 创建人从 JWT token 解析，无需客户端传入
     */
    public CreateContentResponse createContent(CreateContentRequest request) {
        Long creatorId = requireCurrentUserId();
        logger.info("创建内容：userId={}, title={}", creatorId, request.getTitle());
        if (!permissionService.canCreateContent(creatorId)) {
            logger.warn("创建内容权限不足：userId={}", creatorId);
            throw new BusinessException(403, "只有运营人员才能创建内容");
        }
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new BusinessException(400, "创建人不存在"));
        Content content = new Content();
        content.setTitle(request.getTitle());
        content.setContent(request.getContent());
        content.setCreator(creator);
        content.setStatus(ContentStatus.DRAFT);
        Content savedContent = contentRepository.save(content);
        operationLogService.log(creatorId, "CREATE_CONTENT", savedContent.getId());
        logger.info("内容创建成功：contentId={}", savedContent.getId());
        return new CreateContentResponse(savedContent.getId(), savedContent.getStatus());
    }

    /**
     * 编辑内容（仅草稿状态可编辑，仅创建人可编辑）
     * 操作人从 JWT token 解析
     */
    public CreateContentResponse updateContent(Long contentId, UpdateContentRequest request) {
        Long userId = requireCurrentUserId();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(404, "内容不存在"));

        // 权限检查：仅 OPERATOR 且为创建人可编辑
        if (!permissionService.canEditContent(userId, content.getCreator().getId())) {
            throw new BusinessException(403, "只能编辑自己创建的内容");
        }

        // 状态检查
        if (content.getStatus() != ContentStatus.DRAFT) {
            throw new BusinessException(400, "只有草稿状态的内容才能编辑");
        }

        content.setTitle(request.getTitle());
        content.setContent(request.getContent());
        Content updatedContent = contentRepository.save(content);

        // 记录操作日志
        operationLogService.log(userId, "UPDATE_CONTENT", contentId);
        return new CreateContentResponse(updatedContent.getId(), updatedContent.getStatus());
    }

    /**
     * 删除内容（仅草稿状态可删除）
     * 操作人从 JWT token 解析
     */
    public void deleteContent(Long contentId) {
        Long userId = requireCurrentUserId();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(404, "内容不存在"));

        // 权限检查
        if (!permissionService.isContentCreator(userId, content.getCreator().getId())) {
            throw new BusinessException(403, "只能删除自己创建的内容");
        }

        // 状态检查
        if (content.getStatus() != ContentStatus.DRAFT) {
            throw new BusinessException(400, "只有草稿状态的内容才能删除");
        }

        contentRepository.delete(content);
        operationLogService.log(userId, "DELETE_CONTENT", contentId);
    }

    /**
     * 提交审核（草稿 → 待审核）
     * 操作人从 JWT token 解析
     */
    public com.bytedance.content.content.dto.SubmitReviewResponse submitForReview(Long contentId) {
        Long userId = requireCurrentUserId();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(404, "内容不存在"));

        if (!permissionService.isContentCreator(userId, content.getCreator().getId())) {
            throw new BusinessException(403, "只能提交自己创建的内容");
        }

        // 状态检查
        if (content.getStatus() != ContentStatus.DRAFT && content.getStatus() != ContentStatus.REJECTED) {
            throw new BusinessException(400, "只有草稿或拒绝状态的内容才能提交审核");
        }

        content.setStatus(ContentStatus.PENDING);// 状态转换
        Content savedContent = contentRepository.save(content);
        operationLogService.log(userId, "SUBMIT_REVIEW", contentId);
        return new com.bytedance.content.content.dto.SubmitReviewResponse(
                savedContent.getId(), savedContent.getStatus().toString(), "提交审核成功");
    }

    /**
     * 发布内容（审核通过 → 已上线）
     * 操作人从 JWT token 解析
     */
    public CreateContentResponse publishContent(Long contentId) {
        Long userId = requireCurrentUserId();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(404, "内容不存在"));

        if (content.getStatus() != ContentStatus.APPROVED) {
            throw new BusinessException(400, "只有审核通过的内容才能发布");
        }
        if (!permissionService.isContentCreator(userId, content.getCreator().getId())) {
            throw new BusinessException(403, "只能发布自己创建的内容");
        }

        content.setStatus(ContentStatus.ONLINE); // 状态转换为已上线
        Content savedContent = contentRepository.save(content);
        operationLogService.log(userId, "PUBLISH_CONTENT", contentId);
        return new CreateContentResponse(savedContent.getId(), savedContent.getStatus());
    }

    /**
     * 下线内容（已上线 → 已下线）
     * 操作人从 JWT token 解析
     */
    public CreateContentResponse offlineContent(Long contentId) {
        Long userId = requireCurrentUserId();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(404, "内容不存在"));

        if (content.getStatus() != ContentStatus.ONLINE) {
            throw new BusinessException(400, "只有已上线的内容才能下线");
        }
        if (!permissionService.isContentCreator(userId, content.getCreator().getId())) {
            throw new BusinessException(403, "只能下线自己创建的内容");
        }

        content.setStatus(ContentStatus.OFFLINE);
        Content savedContent = contentRepository.save(content);
        operationLogService.log(userId, "OFFLINE_CONTENT", contentId);
        return new CreateContentResponse(savedContent.getId(), savedContent.getStatus());
    }

    /**
     * 分页查询内容列表（creatorId 是可选过滤条件，非当前用户身份）
     */
    public Map<String, Object> listContent(String status, Long creatorId, Integer page, Integer pageSize, String startDate, String endDate) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        ContentStatus statusEnum = (status == null) ? null : ContentStatus.valueOf(status);
        Page<Content> pageResult = contentRepository.findByConditions(statusEnum, creatorId, pageable);

        int total = (int) pageResult.getTotalElements();
        List<com.bytedance.content.content.dto.ContentListItemResponse> data = pageResult.getContent().stream()
                .map(c -> new com.bytedance.content.content.dto.ContentListItemResponse(
                        c.getId(),
                        c.getTitle(),
                        c.getStatus().toString(),
                        c.getCreator().getId(),
                        c.getCreator().getUsername(),
                        DateTimeUtil.format(c.getCreatedAt()),
                        DateTimeUtil.format(c.getUpdatedAt())
                ))
                .collect(Collectors.toList());

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", data);

        return response;
    }
}

