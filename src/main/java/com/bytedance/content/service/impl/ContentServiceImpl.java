package com.bytedance.content.service.impl;

import com.bytedance.content.common.enums.ContentStatus;
import com.bytedance.content.dto.CreateContentRequest;
import com.bytedance.content.dto.CreateContentResponse;
import com.bytedance.content.entity.Content;
import com.bytedance.content.entity.User;
import com.bytedance.content.repository.ContentRepository;
import com.bytedance.content.repository.UserRepository;
import com.bytedance.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {
    
    @Autowired
    private ContentRepository contentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public CreateContentResponse createContent(CreateContentRequest request) {
        // 查询创建人
        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new RuntimeException("创建人不存在"));
        
        // 创建内容
        Content content = new Content();
        content.setTitle(request.getTitle());
        content.setContent(request.getContent());
        content.setCreator(creator);
        content.setStatus(ContentStatus.DRAFT);
        
        // 保存内容
        Content savedContent = contentRepository.save(content);
        
        return new CreateContentResponse(savedContent.getId(), savedContent.getStatus());
    }
}

