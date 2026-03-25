package com.rikka.raymispring.loader;

import com.alibaba.cloud.ai.graph.agent.Agent;

import java.util.List;

public interface AgentLoader {
    List<String> listAgents();
    Agent loadAgent(String name);
}