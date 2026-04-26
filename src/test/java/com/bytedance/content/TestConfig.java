package com.bytedance.content;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestExecutionListener;

/**
 * 测试配置 - 禁用 Mockito 相关监听器以兼容 Java 25
 */
@TestConfiguration
public class TestConfig {
    // 此配置用于禁用自动的 Mockito 配置
}

