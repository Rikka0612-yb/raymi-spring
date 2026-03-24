package com.rikka.raymispring.config.ai;

import com.rikka.raymispring.advisor.ComLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 晏波
 * 2026/3/24 21:40
 */
@Configuration
public class SpringAIConfig {


    @Bean
    public ChatClient.Builder chatClientBuilder(ChatModel dashScopeChatModel, ComLoggerAdvisor loggerAdvisor) {
        // 在 Builder 中预设好你的自定义 Advisor
        return ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(loggerAdvisor);
    }


}
