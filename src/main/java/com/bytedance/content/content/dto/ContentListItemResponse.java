package com.bytedance.content.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentListItemResponse {

    private Long contentId;

    private String title;

    private String status;

    private Long creatorId;

    private String creatorName;

    private String createdAt;

    private String updatedAt;
}

