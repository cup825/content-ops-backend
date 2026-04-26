package com.bytedance.content.content.service;

import com.bytedance.content.audit.repository.AuditRecordRepository;
import com.bytedance.content.common.enums.AuditStatus;
import com.bytedance.content.common.enums.ContentStatus;
import com.bytedance.content.content.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsService {
    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private AuditRecordRepository auditRecordRepository;

    // 方法1：内容各状态数量
    public Map<String, Long> getContentStatusDistribution() {
        Map<String, Long> distribution = new HashMap<>();
        for (ContentStatus status : ContentStatus.values()) {
            long count = contentRepository.countByStatus(status);
            distribution.put(status.name(), count);
        }
        return distribution;
    }

    // 方法2：审核通过率
    public Map<String, Object> getAuditPassRate() {
        long total = auditRecordRepository.count();
        long approved = auditRecordRepository.countByStatus(AuditStatus.APPROVED);
        double passRate = total > 0 ? (double) approved / total * 100 : 0.0;
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("approved", approved);
        result.put("passRate", passRate);
        return result;
    }

    // 方法3：汇总（调用上面两个）
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("contentStatusDistribution", getContentStatusDistribution());
        summary.put("auditPassRate", getAuditPassRate());
        return summary;
    }

    /**
     * 方法4：按时间维度统计发布量
     *
     * 调用方传入开始和结束日期（字符串格式 yyyy-MM-dd），
     * 内部转换为 LocalDateTime 再查询数据库。
     *
     * 例：startDate="2026-04-01", endDate="2026-04-26"
     * 返回：{ "publishedCount": 5, "startDate": "2026-04-01", "endDate": "2026-04-26" }
     */
    public Map<String, Object> getPublishCountByDateRange(String startDate, String endDate) {
        // 把字符串日期转成 LocalDateTime（开始取当天0点，结束取当天23:59:59）
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end   = LocalDate.parse(endDate).atTime(LocalTime.MAX);

        // 查询该时间范围内状态为 APPROVED（审核通过）的内容数
        // APPROVED 代表已通过审核，即"发布量"的统计口径
        long publishedCount = contentRepository.countByStatusAndCreatedAtBetween(
                ContentStatus.APPROVED, start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("publishedCount", publishedCount);
        return result;
    }
}
