package com.rikka.raymispring.agent;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.rikka.raymispring.constant.SystemPromptConstant;
import com.rikka.raymispring.interceptor.ModelPerformanceInterceptor;
import com.rikka.raymispring.interceptor.ToolPerformanceInterceptor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 晏波
 * 2026/3/24 21:58
 */
@Configuration
public class RaymiAgentConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public ReactAgent raymiReactAgent() {
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)
                .build();

        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();
        return ReactAgent.builder()
                .name("Raymi0.1")
                .model(chatModel)
                .systemPrompt(SystemPromptConstant.SYSTEM_PROMPT)
                .saver(new MemorySaver())
                .interceptors(new ModelPerformanceInterceptor(), new ToolPerformanceInterceptor())
                .build();
     }



}
