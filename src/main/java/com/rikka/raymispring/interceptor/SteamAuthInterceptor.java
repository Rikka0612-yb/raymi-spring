package com.rikka.raymispring.interceptor;

import com.rikka.raymispring.config.properties.SteamApiProperties;
import com.rikka.raymispring.exception.SteamApiException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Steam API 鉴权与错误拦截器
 */
@Component
public class SteamAuthInterceptor implements ClientHttpRequestInterceptor {

    private final SteamApiProperties properties;

    public SteamAuthInterceptor(SteamApiProperties properties) {
        this.properties = properties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 根据 Steam 文档：API 密钥可以通过 x-webapi-key HTTP 请求头设置，这样更安全，不会在 URL 中暴露
        if (properties.getKey() != null && !properties.getKey().isEmpty()) {
            request.getHeaders().add("x-webapi-key", properties.getKey());
        }

        ClientHttpResponse response = execution.execute(request, body);

        // 统一处理限流与鉴权错误
        int statusCode = response.getStatusCode().value();
        if (statusCode == 429 || statusCode == 403 || statusCode == 401) {
            String eresultStr = response.getHeaders().getFirst("x-eresult");
            Integer eresult = null;
            if (eresultStr != null) {
                try {
                    eresult = Integer.parseInt(eresultStr);
                } catch (NumberFormatException ignored) {}
            }
            
            throw new SteamApiException(
                    "Steam API Error: HTTP " + statusCode + (eresult != null ? ", x-eresult: " + eresult : ""),
                    statusCode,
                    eresult
            );
        }

        return response;
    }
}
