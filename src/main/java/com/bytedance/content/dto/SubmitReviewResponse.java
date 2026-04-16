package com.bytedance.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitReviewResponse {

    private Long id;

    private String status;

    private String message;
}

