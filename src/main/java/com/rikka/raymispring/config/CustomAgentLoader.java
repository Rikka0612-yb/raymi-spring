package com.rikka.raymispring.config;

import com.alibaba.cloud.ai.agent.studio.loader.AgentLoader;
import com.alibaba.cloud.ai.graph.agent.Agent;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
public class CustomAgentLoader implements AgentLoader {

    private static final Logger log = LoggerFactory.getLogger(CustomAgentLoader.class);
    private final ApplicationContext applicationContext;
    private final Map<String, Agent> agentMap;
    private final Map<String, String> aliasMap;

    public CustomAgentLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.agentMap = discoverAgents();
        this.aliasMap = createAliasMap();
        log.info("CustomAgentLoader initialized. Discovered agents: {}", agentMap.keySet());
        log.info("Alias mappings: {}", aliasMap);
    }

    private Map<String, Agent> discoverAgents() {
        Map<String, Agent> agents = applicationContext.getBeansOfType(Agent.class)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getValue().name(),
                        Map.Entry::getValue,
                        (existing, replacement) -> existing
                ));
        log.debug("Discovered {} agents: {}", agents.size(), agents.keySet());
        return agents;
    }

    private Map<String, String> createAliasMap() {
        Map<String, String> aliases = new HashMap<>();
        // 将 research_agent 映射到 Raymi0.1，以兼容前端默认配置
        aliases.put("research_agent", "Raymi0.1");
        // 可以添加更多别名映射
        return aliases;
    }

    @NotNull
    @Override
    public List<String> listAgents() {
        return List.copyOf(agentMap.keySet());
    }

    @Override
    public Agent loadAgent(String name) {
        log.info("Loading agent with name: {}", name);
        // 首先检查别名映射
        String actualName = aliasMap.getOrDefault(name, name);
        if (!actualName.equals(name)) {
            log.info("Alias mapping found: {} -> {}", name, actualName);
        }
        
        Agent agent = agentMap.get(actualName);
        if (agent == null) {
            log.error("Agent not found: {} (mapped to: {}). Available agents: {}", name, actualName, agentMap.keySet());
            throw new NoSuchElementException("Agent not found: " + name + 
                    (actualName.equals(name) ? "" : " (mapped from alias: " + actualName + ")"));
        }
        log.info("Successfully loaded agent: {}", actualName);
        return agent;
    }
}