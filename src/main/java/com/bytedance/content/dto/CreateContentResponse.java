package com.bytedance.content.dto;

import com.bytedance.content.common.enums.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateContentResponse {
    
    private Long contentId;
    
    private ContentStatus status;
}

