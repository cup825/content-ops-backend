package com.bytedance.content.controller;

import com.bytedance.content.common.vo.ApiResponse;
import com.bytedance.content.dto.*;
import com.bytedance.content.service.AuditService;
import com.bytedance.content.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content")
public class ContentController {
    
    @Autowired
    private ContentService contentService;

    @Autowired
    private AuditService auditService;
    
    /**
     * 创建内容接口
     * URL: /api/content/create
     * 方法: POST
     * 功能：创建内容，初始状态为 DRAFT
     */
    @PostMapping("/create")
    public ApiResponse<CreateContentResponse> createContent(@Valid @RequestBody CreateContentRequest request) {
        CreateContentResponse response = contentService.createContent(request);
        return ApiResponse.success(response);
    }

    /**
     * 编辑内容接口
     * URL: /api/content/{contentId}
     * 方法: PUT
     * 功能：编辑内容（仅草稿状态可编辑）
     * 校验：只允许创建人编辑
     */
    @PutMapping("/{contentId}")
    public ApiResponse<CreateContentResponse> updateContent(
            @PathVariable Long contentId,
            @Valid @RequestBody UpdateContentRequest request,
            @RequestParam(name = "userId") Long userId) {
        CreateContentResponse response = contentService.updateContent(contentId, userId, request);
        return ApiResponse.success(response);
    }

    /**
     * 删除内容接口
     * URL: /api/content/{contentId}
     * 方法: DELETE
     * 功能：删除内容（仅草稿状态可删除）
     * 校验：只允许创建人删除
     */
    @DeleteMapping("/{contentId}")
    public ApiResponse<Object> deleteContent(
            @PathVariable Long contentId,
            @RequestParam(name = "userId") Long userId) {
        contentService.deleteContent(contentId, userId);
        return ApiResponse.success("内容删除成功");
    }

    /**
     * 提交审核接口
     * URL: /api/content/{contentId}/submit-review
     * 方法: POST
     * 功能：提交内容审核，状态从 DRAFT → PENDING
     * 校验：只能提交自己创建的内容且状态为 DRAFT
     */
    @PostMapping("/{contentId}/submit-review")
    public ApiResponse<SubmitReviewResponse> submitForReview(
            @PathVariable Long contentId,
            @RequestParam(name = "userId") Long userId) {
        SubmitReviewResponse response = contentService.submitForReview(contentId, userId);
        return ApiResponse.success(response);
    }

    /**
     * 审核处理接口
     * URL: /api/content/audit
     * 方法: POST
     * 功能：审核内容，通过或驳回
     * 请求参数: {contentId, reviewerId, action, comment}
     * action: APPROVED / REJECTED
     */
    @PostMapping("/audit")
    public ApiResponse<AuditResponse> auditContent(@Valid @RequestBody AuditRequest request) {
        AuditResponse response = auditService.auditContent(request);
        return ApiResponse.success(response);
    }

    /**
     * 发布内容接口
     * URL: /api/content/{contentId}/publish
     * 方法: POST
     * 功能：发布内容，状态从 APPROVED → ONLINE
     */
    @PostMapping("/{contentId}/publish")
    public ApiResponse<CreateContentResponse> publishContent(
            @PathVariable Long contentId,
            @RequestParam(name = "userId") Long userId) {
        CreateContentResponse response = contentService.publishContent(contentId, userId);
        return ApiResponse.success(response);
    }

    /**
     * 下线内容接口
     * URL: /api/content/{contentId}/offline
     * 方法: POST
     * 功能：下线内容，状态从 ONLINE → OFFLINE
     */
    @PostMapping("/{contentId}/offline")
    public ApiResponse<CreateContentResponse> offlineContent(
            @PathVariable Long contentId,
            @RequestParam(name = "userId") Long userId) {
        CreateContentResponse response = contentService.offlineContent(contentId, userId);
        return ApiResponse.success(response);
    }

    /**
     * 内容列表查询接口
     * URL: /api/content/list
     * 方法: GET
     * 功能：查询内容列表，支持筛选和分页
     * 查询参数: status, creatorId, page, pageSize, startDate, endDate
     */
    @GetMapping("/list")
    public ApiResponse<java.util.Map<String, Object>> listContent(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        java.util.Map<String, Object> response = contentService.listContent(status, creatorId, page, pageSize, startDate, endDate);
        return ApiResponse.success(response);
    }
}


