package com.bytedance.content.content.controller;

import com.bytedance.content.content.dto.*;
import com.bytedance.content.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 创建内容
     */
    @PostMapping("/create")
    public CreateContentResponse createContent(@RequestBody CreateContentRequest request) {
        return contentService.createContent(request);
    }

    /**
     * 编辑内容
     */
    @PutMapping("/{contentId}/update")
    public CreateContentResponse updateContent(
            @PathVariable Long contentId,
            @RequestParam Long userId,
            @RequestBody UpdateContentRequest request) {
        return contentService.updateContent(contentId, userId, request);
    }

    /**
     * 删除内容
     */
    @DeleteMapping("/{contentId}")
    public Map<String, String> deleteContent(
            @PathVariable Long contentId,
            @RequestParam Long userId) {
        contentService.deleteContent(contentId, userId);
        return Map.of("message", "内容删除成功");
    }

    /**
     * 提交审核
     */
    @PostMapping("/submit-review")
    public SubmitReviewResponse submitForReview(
            @RequestParam Long contentId,
            @RequestParam Long userId) {
        return contentService.submitForReview(contentId, userId);
    }

    /**
     * 发布内容
     */
    @PostMapping("/publish")
    public CreateContentResponse publishContent(
            @RequestParam Long contentId,
            @RequestParam Long userId) {
        return contentService.publishContent(contentId, userId);
    }

    /**
     * 下线内容
     */
    @PostMapping("/offline")
    public CreateContentResponse offlineContent(
            @RequestParam Long contentId,
            @RequestParam Long userId) {
        return contentService.offlineContent(contentId, userId);
    }

    /**
     * 查询内容列表
     */
    @GetMapping("/list")
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

