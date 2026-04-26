package com.bytedance.content.content;

import com.bytedance.content.audit.repository.AuditRecordRepository;
import com.bytedance.content.common.enums.AuditStatus;
import com.bytedance.content.common.enums.ContentStatus;
import com.bytedance.content.content.repository.ContentRepository;
import com.bytedance.content.content.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * StatisticsService 单元测试
 * 验证统计计算逻辑是否正确（通过率公式、状态分布完整性、时间范围查询）
 */
@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {

    @Mock private ContentRepository contentRepository;
    @Mock private AuditRecordRepository auditRecordRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    // ---- 测试1：状态分布包含所有枚举值，数量准确 ----
    @Test
    public void getContentStatusDistribution_containsAllStatuses() {
        when(contentRepository.countByStatus(ContentStatus.DRAFT)).thenReturn(3L);
        when(contentRepository.countByStatus(ContentStatus.PENDING)).thenReturn(1L);
        when(contentRepository.countByStatus(ContentStatus.APPROVED)).thenReturn(5L);
        when(contentRepository.countByStatus(ContentStatus.REJECTED)).thenReturn(2L);
        when(contentRepository.countByStatus(ContentStatus.ONLINE)).thenReturn(4L);
        when(contentRepository.countByStatus(ContentStatus.OFFLINE)).thenReturn(1L);

        Map<String, Long> result = statisticsService.getContentStatusDistribution();

        // 必须包含所有状态
        assertEquals(ContentStatus.values().length, result.size());
        assertEquals(3L, result.get("DRAFT"));
        assertEquals(5L, result.get("APPROVED"));
        assertEquals(1L, result.get("OFFLINE"));
    }

    // ---- 测试2：审核通过率计算公式正确（8/10 = 80%）----
    @Test
    public void getAuditPassRate_calculatesCorrectly() {
        when(auditRecordRepository.count()).thenReturn(10L);
        when(auditRecordRepository.countByStatus(AuditStatus.APPROVED)).thenReturn(8L);

        Map<String, Object> result = statisticsService.getAuditPassRate();

        assertEquals(10L, result.get("total"));
        assertEquals(8L, result.get("approved"));
        // 使用 delta=0.01 做浮点数比较
        assertEquals(80.0, (Double) result.get("passRate"), 0.01);
    }

    // ---- 测试3：按时间范围统计发布量，返回正确结果 ----
    @Test
    public void getPublishCountByDateRange_returnsCorrectCount() {
        when(contentRepository.countByStatusAndCreatedAtBetween(
                eq(ContentStatus.APPROVED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(5L);

        Map<String, Object> result =
                statisticsService.getPublishCountByDateRange("2026-04-01", "2026-04-26");

        assertEquals(5L, result.get("publishedCount"));
        assertEquals("2026-04-01", result.get("startDate"));
        assertEquals("2026-04-26", result.get("endDate"));
    }
}

