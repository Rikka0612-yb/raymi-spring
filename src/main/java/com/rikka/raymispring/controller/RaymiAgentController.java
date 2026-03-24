package com.rikka.raymispring.controller;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.rikka.raymispring.common.BaseResponse;
import com.rikka.raymispring.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Raymi Agent 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/agent")
public class RaymiAgentController {

    @Resource
    private ReactAgent raymiReactAgent;

    /**
     * 与Raymi Agent进行对话
     *
     * @param request 用户查询请求
     * @return Agent的响应结果
     */
    @PostMapping("/chat")
    public BaseResponse<String> chat(@RequestBody ChatRequest request) {
        try {
            log.info("接收到Agent聊天请求: {}", request.getQuery());
            AssistantMessage response = raymiReactAgent.call(request.getQuery());
            String responseText = response.getText();
            log.info("Agent响应: {}", responseText);
            return ResultUtils.success(responseText);
        } catch (Exception e) {
            log.error("Agent调用失败", e);
            return ResultUtils.error("500", "Agent调用失败: " + e.getMessage());
        }
    }

    /**
     * 聊天请求DTO
     */
    public static class ChatRequest {
        private String query;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }
}