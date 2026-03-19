package com.rikka.raymispring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author 晏波
 * 2025/11/17 14:10
 */
@Slf4j
@Component
public class ConfigLogger implements ApplicationListener<ApplicationReadyEvent> {

    private final Environment environment;

    public ConfigLogger(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        String[] activeProfiles = environment.getActiveProfiles();
        String appName = environment.getProperty("spring.application.name");
        String serverPort = environment.getProperty("server.port");
        // 兼容 WebFlux 环境，server.servlet.context-path 在 Gateway 中可能不存在
        String contextPath = environment.getProperty("server.servlet.context-path");
        if (contextPath == null) {
            contextPath = "";
        }

        log.info("""

                        ==========================================================
                        🚀 应用启动成功!
                        📍 应用名称: {}
                        ✅ 激活环境: {}
                        🔌 端口号:   {}
                        🔗 前置路径: {}
                        ==========================================================""",
                appName, Arrays.toString(activeProfiles), serverPort, contextPath);

        if (activeProfiles.length == 0) {
            log.warn("⚠️  没有激活的配置文件，使用默认配置");
        }
    }
}