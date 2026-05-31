package com.bytedance.content.content.controller;

import com.bytedance.content.common.vo.ApiResponse;
import com.bytedance.content.content.dto.*;
import com.bytedance.content.content.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 创建内容（创建人从 JWT 解析）
     * POST /api/v1/content
     */
    @PostMapping
    public ApiResponse<CreateContentResponse> createContent(@Valid @RequestBody CreateContentRequest request) {
        return ApiResponse.success(contentService.createContent(request));
    }

    /**
     * 编辑内容（操作人从 JWT 解析）
     * PUT /api/v1/content/{contentId}
     */
    @PutMapping("/{contentId}")
    public ApiResponse<CreateContentResponse> updateContent(
            @PathVariable Long contentId,
            @Valid @RequestBody UpdateContentRequest request) {
        return ApiResponse.success(contentService.updateContent(contentId, request));
    }

    /**
     * 删除内容（操作人从 JWT 解析）
     * DELETE /api/v1/content/{contentId}
     */
    @DeleteMapping("/{contentId}")
    public ApiResponse<String> deleteContent(@PathVariable Long contentId) {
        contentService.deleteContent(contentId);
        return ApiResponse.success("内容删除成功", null);
    }

    /**
     * 提交审核（操作人从 JWT 解析）
     * POST /api/v1/content/{contentId}/submit
     */
    @PostMapping("/{contentId}/submit")
    public ApiResponse<SubmitReviewResponse> submitForReview(@PathVariable Long contentId) {
        return ApiResponse.success(contentService.submitForReview(contentId));
    }

    /**
     * 发布内容（操作人从 JWT 解析）
     * POST /api/v1/content/{contentId}/publish
     */
    @PostMapping("/{contentId}/publish")
    public ApiResponse<CreateContentResponse> publishContent(@PathVariable Long contentId) {
        return ApiResponse.success(contentService.publishContent(contentId));
    }

    /**
     * 下线内容（操作人从 JWT 解析）
     * POST /api/v1/content/{contentId}/offline
     */
    @PostMapping("/{contentId}/offline")
    public ApiResponse<CreateContentResponse> offlineContent(@PathVariable Long contentId) {
        return ApiResponse.success(contentService.offlineContent(contentId));
    }

    /**
     * 查询内容列表
     * GET /api/v1/content?status=DRAFT&creatorId=1&page=1&pageSize=10
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> listContent(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ApiResponse.success(contentService.listContent(status, creatorId, page, pageSize, startDate, endDate));
    }
}
