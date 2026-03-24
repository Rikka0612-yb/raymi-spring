package com.rikka.raymispring.runner;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.streaming.OutputType;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Scanner;

/**
 * 交互式控制台运行器
 * 仅在 dev 环境下生效，避免影响生产环境
 */
@Component
@Profile("dev")
public class InteractiveAgentRunner implements CommandLineRunner {

    @Autowired
    private ReactAgent raymiReactAgent;

    @Override
    public void run(String... args) throws Exception {
        // 使用一个新线程来运行交互式控制台，避免阻塞 Spring Boot 的启动主线程
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n==================================================");
            System.out.println("Raymi 交互式控制台已启动！(输入 'exit' 或 'quit' 退出)");
            System.out.println("==================================================");

            while (true) {
                System.out.print("\n你: ");
                // 检查是否有下一行输入
                if (!scanner.hasNextLine()) {
                    break;
                }
                
                String query = scanner.nextLine();

                if ("exit".equalsIgnoreCase(query.trim()) || "quit".equalsIgnoreCase(query.trim())) {
                    System.out.println("Raymi: 哼，今天就勉为其难陪你聊到这里吧，下次再见咯！");
                    System.out.println("退出交互模式...");
                    break;
                }

                if (query.trim().isEmpty()) {
                    continue;
                }

                System.out.print("Raymi: ");
                
                try {
                    // 发起流式调用
                    Flux<NodeOutput> stream = raymiReactAgent.stream(query);
                    
                    // 使用 doOnNext 打印流式片段，并使用 blockLast() 阻塞当前线程等待回答结束
                    stream.doOnNext(output -> {
                        if (output instanceof StreamingOutput streamingOutput) {
                            OutputType type = streamingOutput.getOutputType();
                            // 过滤出模型正在推理的增量内容
                            if (type == OutputType.AGENT_MODEL_STREAMING) {
                                String text = streamingOutput.message().getText();
                                if (text != null) {
                                    // 将接收到的文本逐字打印，实现更细腻的打字机效果
                                    for (char c : text.toCharArray()) {
                                        System.out.print(c);
                                        try {
                                            // 每个字符停顿 20 毫秒
                                            Thread.sleep(20);
                                        } catch (InterruptedException e) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }
                            }
                        }
                    }).blockLast();
                    
                } catch (Exception e) {
                    System.err.println("\n[发生错误]: " + e.getMessage());
                }
                System.out.println(); // 换行，准备下一次输入
            }
        }, "Interactive-Console-Thread").start();
    }
}
