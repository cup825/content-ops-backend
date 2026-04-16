package com.bytedance.content.content.service;

import com.bytedance.content.content.entity.OperationLog;
import com.bytedance.content.content.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OperationLogService {

    @Autowired
    private OperationLogRepository operationLogRepository;

    /**
     * 记录操作日志
     */
    public void log(Long userId, String action, Long targetId) {
        OperationLog operationLog = new OperationLog();
        operationLog.setUserId(userId);
        operationLog.setAction(action);
        operationLog.setTargetId(targetId);
        operationLogRepository.save(operationLog);
    }
}

