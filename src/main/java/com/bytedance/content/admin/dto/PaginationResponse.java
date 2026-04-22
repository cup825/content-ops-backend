package com.bytedance.content.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应
 * 用途：统一返回分页数据
 * 
 * 返回示例：
 * {
 *   "content": [...],      // 当前页数据
 *   "total": 100,          // 总共有100条数据
 *   "page": 1,             // 当前是第1页
 *   "pageSize": 10,        // 每页10条
 *   "totalPages": 10       // 共10页
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> {

    private List<T> content;     // 当前页数据
    private long total;          // 总条数
    private int page;            // 当前页码
    private int pageSize;        // 每页数量
    private int totalPages;      // 总页数
}

