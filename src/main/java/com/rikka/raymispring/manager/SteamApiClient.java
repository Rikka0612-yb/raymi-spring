package com.rikka.raymispring.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikka.raymispring.config.properties.SteamApiProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class SteamApiClient {

    private final RestTemplate restTemplate;
    private final SteamApiProperties properties;
    private final ObjectMapper objectMapper;

    public SteamApiClient(@Qualifier("steamRestTemplate") RestTemplate restTemplate,
                          SteamApiProperties properties,
                          ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * 通用 GET 请求
     * 直接映射到传入的 responseType，不再进行逻辑剥离
     */
    public <T> T serviceGet(String interfaceName, String method, String version,
                            MultiValueMap<String, String> queryParams, Class<T> responseType) {
        if (queryParams == null) {
            queryParams = new LinkedMultiValueMap<>();
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .pathSegment(interfaceName, method, version, "")
                .queryParams(queryParams)
                .build()
                .toUri();

        return executeRequest(uri, HttpMethod.GET, null, responseType);
    }

    /**
     * 通用 POST Service 请求
     */
    public <T> T servicePost(String interfaceName, String method, String version,
                             Object inputJsonObj, String accessToken, Class<T> responseType) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("input_json", objectMapper.writeValueAsString(inputJsonObj));
            if (accessToken != null && !accessToken.isEmpty()) {
                formData.add("access_token", accessToken);
            }

            URI uri = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .pathSegment(interfaceName, method, version, "")
                    .build()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            return executeRequest(uri, HttpMethod.POST, new HttpEntity<>(formData, headers), responseType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to prepare Steam Service API request", e);
        }
    }

    /**
     * 发送 Storefront API (商店) 请求 (store.steampowered.com)
     * 适用于 api/appdetails 等接口
     */
    public <T> T pathGet(String apiPath, MultiValueMap<String, String> queryParams, Class<T> responseType) {
        if (queryParams == null) {
            queryParams = new LinkedMultiValueMap<>();
        }

        // 处理路径斜杠，确保拼接正确
        String path = apiPath.startsWith("/") ? apiPath : "/" + apiPath;

        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getStoreBaseUrl())
                .path(path)
                .queryParams(queryParams)
                .build()
                .toUri();

        return executeRequest(uri, HttpMethod.GET, null, responseType);
    }

    /**
     * 核心执行方法
     */
    private <T> T executeRequest(URI uri, HttpMethod method, HttpEntity<?> entity, Class<T> responseType) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, method, entity, String.class);
            if (response.getBody() == null) return null;
            return objectMapper.readValue(response.getBody(), responseType);
        } catch (Exception e) {
            throw new RuntimeException("Steam API interaction failed: " + uri, e);
        }
    }
}