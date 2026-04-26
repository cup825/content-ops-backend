package com.bytedance.content.audit.repository;

import com.bytedance.content.audit.entity.AuditRecord;
import com.bytedance.content.common.enums.AuditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {
    // 按审核状态统计数量
    long countByStatus(AuditStatus status);
}

