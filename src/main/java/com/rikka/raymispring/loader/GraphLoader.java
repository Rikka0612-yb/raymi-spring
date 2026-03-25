package com.rikka.raymispring.loader;

import com.alibaba.cloud.ai.graph.CompiledGraph;

import java.util.List;

public interface GraphLoader {
    List<String> listGraphs();
    CompiledGraph loadGraph(String name);
}