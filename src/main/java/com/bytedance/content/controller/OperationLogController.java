package com.bytedance.content.controller;

import com.bytedance.content.common.vo.ApiResponse;
import com.bytedance.content.dto.OperationLogResponse;
import com.bytedance.content.entity.OperationLog;
import com.bytedance.content.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作日志控制器
 * 提供操作日志的查询功能
 */
@RestController
@RequestMapping("/api/operation-log")
public class OperationLogController {
    
    @Autowired
    private OperationLogRepository operationLogRepository;
    
    /**
     * 获取操作日志列表（分页）
     * URL: /api/operation-log/list
     * 方法: GET
     * 查询参数: page, pageSize
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getOperationLogs(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        
        // Spring Data 的 page 从 0 开始，所以需要减 1
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        var operationLogs = operationLogRepository.findAll(pageable);
        
        List<OperationLogResponse> data = operationLogs.getContent().stream()
                .map(log -> new OperationLogResponse(
                        log.getId(),
                        log.getUser().getId(),
                        log.getUser().getUsername(),
                        log.getAction(),
                        log.getTargetId(),
                        log.getCreatedAt()
                ))
                .collect(Collectors.toList());
        
        Map<String, Object> response = Map.of(
                "total", operationLogs.getTotalElements(),
                "page", page,
                "pageSize", pageSize,
                "data", data
        );
        
        return ApiResponse.success(response);
    }
}

