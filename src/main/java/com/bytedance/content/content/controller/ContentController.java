package com.bytedance.content.content.controller;

import com.bytedance.content.content.dto.*;
import com.bytedance.content.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 创建内容（操作人从 JWT 解析）
     * POST /api/v1/content
     */
    @PostMapping
    public CreateContentResponse createContent(@RequestBody CreateContentRequest request) {
        return contentService.createContent(request);
    }

    /**
     * 编辑内容（操作人从 JWT 解析）
     * PUT /api/v1/content/{contentId}
     */
    @PutMapping("/{contentId}")
    public CreateContentResponse updateContent(
            @PathVariable Long contentId,
            @RequestBody UpdateContentRequest request) {
        return contentService.updateContent(contentId, request);
    }

    /**
     * 删除内容（操作人从 JWT 解析）
     * DELETE /api/v1/content/{contentId}
     */
    @DeleteMapping("/{contentId}")
    public Map<String, String> deleteContent(@PathVariable Long contentId) {
        contentService.deleteContent(contentId);
        return Map.of("message", "内容删除成功");
    }

    /**
     * 提交审核（操作人从 JWT 解析）
     * POST /api/v1/content/{contentId}/submit
     */
    @PostMapping("/{contentId}/submit")
    public SubmitReviewResponse submitForReview(@PathVariable Long contentId) {
        return contentService.submitForReview(contentId);
    }

    /**
     * 发布内容（操作人从 JWT 解析）
     * POST /api/v1/content/{contentId}/publish
     */
    @PostMapping("/{contentId}/publish")
    public CreateContentResponse publishContent(@PathVariable Long contentId) {
        return contentService.publishContent(contentId);
    }

    /**
     * 下线内容（操作人从 JWT 解析）
     * POST /api/v1/content/{contentId}/offline
     */
    @PostMapping("/{contentId}/offline")
    public CreateContentResponse offlineContent(@PathVariable Long contentId) {
        return contentService.offlineContent(contentId);
    }

    /**
     * 查询内容列表
     * GET /api/v1/content?status=DRAFT&creatorId=1&page=1&pageSize=10
     */
    @GetMapping
    public Map<String, Object> listContent(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return contentService.listContent(status, creatorId, page, pageSize, startDate, endDate);
    }
}
