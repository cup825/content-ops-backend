package com.bytedance.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogResponse {

    private Long id;

    private Long userId;

    private String username;

    private String action;

    private Long targetId;

    private LocalDateTime createdAt;
}

