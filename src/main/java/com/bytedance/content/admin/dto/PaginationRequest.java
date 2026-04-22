package com.bytedance.content.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页请求参数
 * 用途：提供统一的分页、排序、搜索参数
 * 
 * 示例：page=1&pageSize=10&sortBy=id&sortOrder=desc&searchKey=admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {

    private Integer page;       // 页码（从 1 开始）
    private Integer pageSize;   // 每页条数
    private String sortBy;      // 排序字段（如：id, username, createdAt）
    private String sortOrder;   // 排序方向：asc 或 desc

    /**
     * 获取JPA需要的页码（从0开始）
     */
    public int getPageNumber() {
        return page != null && page > 0 ? page - 1 : 0;
    }

    /**
     * 获取每页条数（默认10条）
     */
    public int getPageSize() {
        return pageSize != null && pageSize > 0 ? pageSize : 10;
    }

    /**
     * 获取排序方向（默认升序）
     */
    public String getSortOrder() {
        return "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";
    }
}

