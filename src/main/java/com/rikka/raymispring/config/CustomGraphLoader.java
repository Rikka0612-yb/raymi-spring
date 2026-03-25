package com.rikka.raymispring.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.rikka.raymispring.loader.GraphLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class CustomGraphLoader implements GraphLoader {

    private static final Logger log = LoggerFactory.getLogger(CustomGraphLoader.class);
    private final ApplicationContext applicationContext;
    private final Map<String, CompiledGraph> graphMap;

    public CustomGraphLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.graphMap = discoverGraphs();
        log.info("CustomGraphLoader initialized. Discovered graphs: {}", graphMap.keySet());
    }

    private Map<String, CompiledGraph> discoverGraphs() {
        @SuppressWarnings("rawtypes")
        Map<String, CompiledGraph> graphs = applicationContext.getBeansOfType(CompiledGraph.class);
        return graphs.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue(),
                (existing, replacement) -> existing
        ));
    }

    @NotNull
    @Override
    public List<String> listGraphs() {
        return List.copyOf(graphMap.keySet());
    }

    @Override
    public CompiledGraph loadGraph(String name) {
        log.info("Loading graph with name: {}", name);
        CompiledGraph graph = graphMap.get(name);
        if (graph == null) {
            log.error("Graph not found: {}. Available graphs: {}", name, graphMap.keySet());
            throw new NoSuchElementException("Graph not found: " + name);
        }
        return graph;
    }
}
