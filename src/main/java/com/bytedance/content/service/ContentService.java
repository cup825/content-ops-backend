package com.bytedance.content.service;

import com.bytedance.content.dto.CreateContentRequest;
import com.bytedance.content.dto.CreateContentResponse;

public interface ContentService {

    /**
     * 创建内容，初始状态为 DRAFT
     */
    CreateContentResponse createContent(CreateContentRequest request);
}

