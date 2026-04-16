package com.bytedance.content.content.dto;

import com.bytedance.content.common.enums.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateContentResponse {
    
    private Long contentId;
    
    private ContentStatus status;
}

