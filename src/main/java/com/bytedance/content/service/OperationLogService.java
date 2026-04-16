package com.bytedance.content.service;

import com.bytedance.content.entity.OperationLog;
import com.bytedance.content.entity.User;
import com.bytedance.content.repository.OperationLogRepository;
import com.bytedance.content.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 操作日志服务
 * 记录系统中的关键操作行为
 */
@Service
@Transactional
public class OperationLogService {
    
    @Autowired
    private OperationLogRepository operationLogRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 记录操作日志
     * 
     * @param userId 操作人ID
     * @param action 操作行为（如：CREATE_CONTENT, UPDATE_CONTENT, DELETE_CONTENT 等）
     * @param targetId 操作目标ID（如：内容ID）
     */
    public void log(Long userId, String action, Long targetId) {
        User user = userRepository.findById(userId)
                .orElse(null);
        
        if (user == null) {
            return; // 用户不存在时不记录
        }
        
        OperationLog log = new OperationLog();
        log.setUser(user);
        log.setAction(action);
        log.setTargetId(targetId);
        
        operationLogRepository.save(log);
    }
    
    /**
     * 简化版本的日志记录（不需要 targetId）
     */
    public void log(Long userId, String action) {
        log(userId, action, null);
    }
}

