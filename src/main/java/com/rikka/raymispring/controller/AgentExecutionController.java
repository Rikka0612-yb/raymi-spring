package com.rikka.raymispring.controller;

import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import com.alibaba.cloud.ai.graph.agent.Agent;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import com.alibaba.cloud.ai.graph.streaming.OutputType;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikka.raymispring.loader.AgentLoader;
import com.rikka.raymispring.model.dto.AgentResumeRequest;
import com.rikka.raymispring.model.dto.AgentRunRequest;
import com.rikka.raymispring.model.dto.messages.AgentRunResponse;
import com.rikka.raymispring.model.dto.messages.MessageDTO;
import com.rikka.raymispring.model.dto.messages.ToolRequestConfirmMessageDTO;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/studio/agent")
public class AgentExecutionController {

    private static final Logger log = LoggerFactory.getLogger(AgentExecutionController.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final AgentLoader agentLoader;

    @Autowired
    public AgentExecutionController(AgentLoader agentLoader) {
        this.agentLoader = agentLoader;
    }

    @PostMapping(value = "/run_sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> agentRunSse(@RequestBody AgentRunRequest request) {
        if (request.appName == null || request.appName.trim().isEmpty()) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "appName cannot be null or empty"));
        }
        if (request.threadId == null || request.threadId.trim().isEmpty()) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "threadId cannot be null or empty"));
        }

        try {
            Agent agent = agentLoader.loadAgent(request.appName);
            RunnableConfig runnableConfig = RunnableConfig.builder()
                    .threadId(request.threadId != null ? request.threadId : "default_thread")
                    .addMetadata("user_id", request.userId != null ? request.userId : "default_user")
                    .build();

            return executeAgent(request.newMessage.toUserMessage(), agent, runnableConfig);
        } catch (Exception e) {
            log.error("Error during agent run", e);
            return Flux.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Agent run failed", e));
        }
    }

    @PostMapping(value = "/resume_sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> agentResumeSse(@RequestBody AgentResumeRequest request) {
        if (request.appName == null || request.appName.trim().isEmpty()) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "appName cannot be null or empty"));
        }
        if (request.threadId == null || request.threadId.trim().isEmpty()) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "threadId cannot be null or empty"));
        }

        try {
            Agent agent = agentLoader.loadAgent(request.appName);
            InterruptionMetadata.Builder metadataBuilder = InterruptionMetadata.builder();

            if (request.toolFeedbacks != null && !request.toolFeedbacks.isEmpty()) {
                for (ToolRequestConfirmMessageDTO.ToolFeedback toolFeedback : request.toolFeedbacks) {
                    InterruptionMetadata.ToolFeedback.FeedbackResult result = toolFeedback.getResult() != null
                            ? InterruptionMetadata.ToolFeedback.FeedbackResult.valueOf(toolFeedback.getResult().name())
                            : InterruptionMetadata.ToolFeedback.FeedbackResult.APPROVED;

                    InterruptionMetadata.ToolFeedback feedback = new InterruptionMetadata.ToolFeedback(
                            toolFeedback.getId(),
                            toolFeedback.getName(),
                            toolFeedback.getArguments(),
                            result,
                            toolFeedback.getDescription()
                    );
                    metadataBuilder.addToolFeedback(feedback);
                }
            }

            RunnableConfig runnableConfig = RunnableConfig.builder()
                    .threadId(request.threadId != null ? request.threadId : "default_thread")
                    .addMetadata("user_id", request.userId != null ? request.userId : "default_user")
                    .addHumanFeedback(metadataBuilder.build())
                    .build();

            return executeAgent(null, agent, runnableConfig);
        } catch (Exception e) {
            log.error("Error during agent resume", e);
            return Flux.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Agent resume failed", e));
        }
    }

    @NotNull
    private Flux<ServerSentEvent<String>> executeAgent(UserMessage userMessage, Agent agent, RunnableConfig runnableConfig) throws Exception {
        Flux<NodeOutput> agentStream = (userMessage != null) ? agent.stream(userMessage, runnableConfig) : agent.stream("", runnableConfig);

        return agentStream.map(nodeOutput -> {
            String node = nodeOutput.node();
            String agentName = nodeOutput.agent();
            Usage tokenUsage = nodeOutput.tokenUsage();

            AgentRunResponse agentResponse = null;

            if (nodeOutput instanceof StreamingOutput<?> streamingOutput) {
                Message message = streamingOutput.message();
                if (message == null) {
                    return ServerSentEvent.<String>builder().data("{}").build();
                }

                if (streamingOutput.getOutputType() == OutputType.AGENT_MODEL_STREAMING) {
                    if (message instanceof AssistantMessage assistantMessage) {
                        if (assistantMessage.hasToolCalls()) {
                            agentResponse = new AgentRunResponse(node, "tool_request", agentName, assistantMessage, tokenUsage, "");
                        } else {
                            // 流式块，只发送 chunk，不发送 message
                            agentResponse = new AgentRunResponse(node, "chunk", agentName, (Message) null, tokenUsage, assistantMessage.getText());
                        }
                    } else {
                        agentResponse = new AgentRunResponse(node, "chunk", agentName, (Message) null, tokenUsage, message.getText());
                    }
                } else if (streamingOutput.getOutputType() == OutputType.AGENT_MODEL_FINISHED) {
                    // 完成事件，发送完整的 message，不发送 chunk
                    agentResponse = new AgentRunResponse(node, "message", agentName, message, tokenUsage, null);
                } else {
                    agentResponse = new AgentRunResponse(node, "message", agentName, message, tokenUsage, null);
                }
            } else if (nodeOutput instanceof InterruptionMetadata interruptionMetadata) {
                ToolRequestConfirmMessageDTO toolRequestMessage = MessageDTO.MessageDTOFactory.fromInterruptionMetadata(interruptionMetadata);
                agentResponse = new AgentRunResponse(node, "interruption", agentName, toolRequestMessage, tokenUsage, null);
            }

            try {
                if (agentResponse != null) {
                    return ServerSentEvent.<String>builder().data(mapper.writeValueAsString(agentResponse)).build();
                }
            } catch (Exception e) {
                log.error("Failed to serialize AgentRunResponse", e);
            }
            return ServerSentEvent.<String>builder().data("{}").build();
        }).onErrorResume(error -> {
            log.error("Error occurred during agent stream execution", error);
            String errorJson = String.format("{\"error\":true,\"errorMessage\":\"%s\"}", 
                    error.getMessage() != null ? error.getMessage().replace("\"", "\\\"").replace("\n", "\\n") : "Unknown error");
            return Flux.<ServerSentEvent<String>>just(ServerSentEvent.<String>builder().event("error").data(errorJson).build());
        });
    }
}
