package com.rikka.raymispring.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import com.alibaba.cloud.ai.graph.streaming.OutputType;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikka.raymispring.loader.GraphLoader;
import com.rikka.raymispring.model.dto.AgentResumeRequest;
import com.rikka.raymispring.model.dto.GraphRunRequest;
import com.rikka.raymispring.model.dto.GraphRunResponse;
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

import java.util.Map;

@RestController
@RequestMapping("/api/studio/graph")
public class GraphExecutionController {

    private static final Logger log = LoggerFactory.getLogger(GraphExecutionController.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final GraphLoader graphLoader;

    @Autowired
    public GraphExecutionController(GraphLoader graphLoader) {
        this.graphLoader = graphLoader;
    }

    @PostMapping(value = "/run_sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> graphRunSse(@RequestBody GraphRunRequest request) {
        if (request.graphName == null || request.graphName.trim().isEmpty()) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "graphName cannot be null or empty"));
        }
        if (request.threadId == null || request.threadId.trim().isEmpty()) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "threadId cannot be null or empty"));
        }

        try {
            CompiledGraph graph = graphLoader.loadGraph(request.graphName);
            RunnableConfig runnableConfig = RunnableConfig.builder()
                    .threadId(request.threadId != null ? request.threadId : "default_thread")
                    .addMetadata("user_id", request.userId != null ? request.userId : "default_user")
                    .build();

            return executeGraph(request.newMessage != null ? request.newMessage.toUserMessage() : null, graph, runnableConfig, request.inputs);
        } catch (Exception e) {
            log.error("Error during graph run", e);
            return Flux.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Graph run failed", e));
        }
    }

    @PostMapping(value = "/resume_sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> graphResumeSse(@RequestBody AgentResumeRequest request) {
        if (request.appName == null || request.appName.trim().isEmpty()) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "appName cannot be null or empty"));
        }
        if (request.threadId == null || request.threadId.trim().isEmpty()) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "threadId cannot be null or empty"));
        }

        try {
            CompiledGraph graph = graphLoader.loadGraph(request.appName);
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

            return executeGraph(null, graph, runnableConfig, null);
        } catch (Exception e) {
            log.error("Error during graph resume", e);
            return Flux.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Graph resume failed", e));
        }
    }

    @NotNull
    private Flux<ServerSentEvent<String>> executeGraph(UserMessage userMessage, CompiledGraph graph, RunnableConfig runnableConfig, Map<String, Object> stateDelta) throws Exception {
        Map<String, Object> inputs;
        if (stateDelta != null) {
            inputs = new java.util.HashMap<>(stateDelta);
        } else {
            String content = userMessage != null ? userMessage.getText() : "";
            inputs = Map.of("input", content, "messages", java.util.List.of(new UserMessage(content)));
        }
        Flux<NodeOutput> graphStream = graph.stream(inputs, runnableConfig);

        return graphStream.map(nodeOutput -> {
            String node = nodeOutput.node();
            String agentName = nodeOutput.agent();
            Usage tokenUsage = nodeOutput.tokenUsage();
            Map<String, Object> state = null;
            if (nodeOutput.state() != null && nodeOutput.state().data() != null) {
                Object data = nodeOutput.state().data();
                if (data instanceof Map) {
                    state = (Map<String, Object>) data;
                } else {
                    state = mapper.convertValue(data, new TypeReference<Map<String, Object>>() {});
                }
            }

            GraphRunResponse graphResponse = null;

            if (nodeOutput instanceof StreamingOutput<?> streamingOutput) {
                Message message = streamingOutput.message();
                if (message == null) {
                    return ServerSentEvent.<String>builder().data("{}").build();
                }

                if (streamingOutput.getOutputType() == OutputType.AGENT_MODEL_STREAMING) {
                    if (message instanceof AssistantMessage assistantMessage) {
                        if (assistantMessage.hasToolCalls()) {
                            graphResponse = new GraphRunResponse(node, "tool_request", agentName, assistantMessage, tokenUsage, "", state);
                        } else {
                            graphResponse = new GraphRunResponse(node, "chunk", agentName, (Message) null, tokenUsage, assistantMessage.getText(), state);
                        }
                    } else {
                        graphResponse = new GraphRunResponse(node, "chunk", agentName, (Message) null, tokenUsage, message.getText(), state);
                    }
                } else if (streamingOutput.getOutputType() == OutputType.AGENT_MODEL_FINISHED) {
                    graphResponse = new GraphRunResponse(node, "message", agentName, message, tokenUsage, null, state);
                } else {
                    graphResponse = new GraphRunResponse(node, "message", agentName, message, tokenUsage, null, state);
                }
            } else if (nodeOutput instanceof InterruptionMetadata interruptionMetadata) {
                ToolRequestConfirmMessageDTO toolRequestMessage = MessageDTO.MessageDTOFactory.fromInterruptionMetadata(interruptionMetadata);
                graphResponse = new GraphRunResponse(node, "interruption", agentName, toolRequestMessage, tokenUsage, null, state);
            }

            try {
                if (graphResponse != null) {
                    return ServerSentEvent.<String>builder().data(mapper.writeValueAsString(graphResponse)).build();
                }
            } catch (Exception e) {
                log.error("Failed to serialize GraphRunResponse", e);
            }
            return ServerSentEvent.<String>builder().data("{}").build();
        }).onErrorResume(error -> {
            log.error("Error occurred during graph stream execution", error);
            String errorJson = String.format("{\"error\":true,\"errorMessage\":\"%s\"}", 
                    error.getMessage() != null ? error.getMessage().replace("\"", "\\\"").replace("\n", "\\n") : "Unknown error");
            return Flux.<ServerSentEvent<String>>just(ServerSentEvent.<String>builder().event("error").data(errorJson).build());
        });
    }
}
