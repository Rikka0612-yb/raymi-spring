package com.rikka.raymispring.config;

import com.rikka.raymispring.config.properties.SteamApiProperties;
import com.rikka.raymispring.constant.CommonConstants;
import com.rikka.raymispring.interceptor.SteamAuthInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.time.Duration;

@Slf4j
@Configuration
public class SteamRestTemplateConfig {

    @Bean(name = "steamRestTemplate")
    public RestTemplate steamRestTemplate(RestTemplateBuilder builder,
                                          SteamAuthInterceptor authInterceptor,
                                          SteamApiProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 尝试获取代理配置
        Proxy proxy = resolveProxy(properties);
        if (proxy != null) {
            factory.setProxy(proxy);
        }
        return builder
                .requestFactory(() -> factory)
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .interceptors(authInterceptor)
                .build();
    }

    /**
     * 代理识别逻辑
     */
    private Proxy resolveProxy(SteamApiProperties properties) {
        // 1. 优先从 application.yml 配置中读取（推荐由 UI 界面写入配置）
        if (properties.getProxyHost() != null && properties.getProxyPort() != null) {
            return new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(properties.getProxyHost(), properties.getProxyPort()));
        }

        // 2. 自动探测常见的本地代理端口 (Clash/V2Ray/WattToolkit)
        int[] commonPorts = {7890, 10809, 1080, 10808};
        for (int port : commonPorts) {
            if (isLocalPortActive(port)) {
                // 发现可用端口，自动返回
                log.info("Found active proxy port: {}", port);
                return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(CommonConstants.LOCAL_HOST, port));
            }
        }

        return null;
    }

    /**
     * 简单的 Socket 检测，判断本地端口是否开启
     */
    private boolean isLocalPortActive(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(CommonConstants.LOCAL_HOST, port), 200); // 200ms 快速探测
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}