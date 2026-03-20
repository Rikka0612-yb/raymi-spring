package com.rikka.raymispring.steam.config;

import com.rikka.raymispring.steam.client.SteamAuthInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class SteamRestTemplateConfig {

    @Bean(name = "steamRestTemplate")
    public RestTemplate steamRestTemplate(RestTemplateBuilder builder, SteamAuthInterceptor authInterceptor) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .interceptors(authInterceptor)
                .build();
    }
}
