package com.bytedance.content.controller;

import com.bytedance.content.common.vo.ApiResponse;
import com.bytedance.content.dto.CreateContentRequest;
import com.bytedance.content.dto.CreateContentResponse;
import com.bytedance.content.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/content")
public class ContentController {
    
    @Autowired
    private ContentService contentService;
    
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
}
