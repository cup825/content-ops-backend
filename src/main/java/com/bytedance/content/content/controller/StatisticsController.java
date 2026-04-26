package com.bytedance.content.content.controller;

import com.bytedance.content.content.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {
    @Autowired
    StatisticsService statisticsService;

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return statisticsService.getSummary();
    }

    /**
     * 按时间范围统计发布量
     * 示例：GET /api/stats/publish?startDate=2026-04-01&endDate=2026-04-26
     */
    @GetMapping("/publish")
    public Map<String, Object> getPublishCount(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return statisticsService.getPublishCountByDateRange(startDate, endDate);
    }
}
