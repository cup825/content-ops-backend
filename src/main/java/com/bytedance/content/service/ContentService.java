package com.bytedance.content.service;

import com.bytedance.content.dto.CreateContentRequest;
import com.bytedance.content.dto.CreateContentResponse;
import com.bytedance.content.dto.UpdateContentRequest;
import com.bytedance.content.dto.SubmitReviewResponse;
import com.bytedance.content.dto.ContentListItemResponse;
import java.util.List;
import java.util.Map;

public interface ContentService {

    /**
     * 创建内容，初始状态为 DRAFT
     */
    CreateContentResponse createContent(CreateContentRequest request);

    /**
     * 编辑内容（仅草稿状态可编辑）
     */
    CreateContentResponse updateContent(Long contentId, Long userId, UpdateContentRequest request);

    /**
     * 删除内容（仅草稿状态可删除）
     */
    void deleteContent(Long contentId, Long userId);

    /**
     * 提交审核（草稿 → 待审核）
     */
    SubmitReviewResponse submitForReview(Long contentId, Long userId);

    /**
     * 发布内容（审核通过 → 已上线）
     */
    CreateContentResponse publishContent(Long contentId, Long userId);

    /**
     * 下线内容（已上线 → 已下线）
     */
    CreateContentResponse offlineContent(Long contentId, Long userId);

    /**
     * 查询内容列表（支持筛选和分页）
     */
    Map<String, Object> listContent(String status, Long creatorId, Integer page, Integer pageSize, String startDate, String endDate);
}

