package com.rikka.raymispring.service;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.rikka.raymispring.RaymiSpringApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;

/**
 * @author 晏波
 * 2026/3/24 23:04
 */
@Slf4j
@ActiveProfiles({"dev", "secret"})
@SpringBootTest(classes = RaymiSpringApplication.class)
public class AITest {

    @Autowired
    private ReactAgent raymiReactAgent;

    @Test
    void test() throws GraphRunnerException {
        AssistantMessage response = raymiReactAgent.call("hello!");
        String text = response.getText();
        System.out.println(text);
    }

    @Test
    void test2() throws GraphRunnerException {
        // 示例10.1：基础流式调用
        Flux<NodeOutput> stream = raymiReactAgent.stream("hello");
        // 流式打印模型响应
        stream.subscribe(output -> {
            if (!output.isSTART() && !output.isEND()) {
                System.out.print(output.node());
            }
        });
    }
}
