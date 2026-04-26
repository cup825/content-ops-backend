package com.bytedance.content.content.controller;

import com.bytedance.content.content.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
