package com.rikka.raymispring.tool;

import com.rikka.raymispring.config.properties.SteamApiProperties;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 晏波
 * 2026/3/25 20:53
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.base-url}")
    private String searchApiBaseUrl;

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Autowired
    private SteamApiProperties steamApiProperties;

    @Bean
    public ToolCallback[] allTools() {
        WebSearchTool webSearchTool = new WebSearchTool(searchApiBaseUrl, searchApiKey,
                steamApiProperties.getProxyHost(), steamApiProperties.getProxyPort());
        DateTimeTools dateTimeTools = new DateTimeTools();
        return ToolCallbacks.from(
                webSearchTool,
                dateTimeTools
        );
    }
}

