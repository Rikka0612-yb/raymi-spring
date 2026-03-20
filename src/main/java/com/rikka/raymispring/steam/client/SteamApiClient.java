package com.rikka.raymispring.steam.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikka.raymispring.steam.config.SteamApiProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Steam API 封装客户端
 */
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
     * 发送标准的 GET 请求
     * 适用于绝大多数常规接口（例如 GetPlayerSummaries, GetOwnedGames）
     * 自动剥离最外层的 {"response": { ... }}，并映射到目标 class。
     *
     * @param interfaceName 接口名称，如 "ISteamUser"
     * @param method        方法名称，如 "GetPlayerSummaries"
     * @param version       版本，如 "v2"
     * @param queryParams   查询参数
     * @param responseType  希望解析到的目标实体类型（即 {"response": ...} 内层的数据结构）
     */
    public <T> T get(String interfaceName, String method, String version,
                     MultiValueMap<String, String> queryParams, Class<T> responseType) {
        if (queryParams == null) {
            queryParams = new LinkedMultiValueMap<>();
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .pathSegment(interfaceName, method, version, "") // 最后一个 "" 会在 URL 末尾加上斜杠 "/"
                .queryParams(queryParams)
                .build()
                .toUri();

        // 1. 获取原始 JSON 字符串
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        
        // 2. 利用 Jackson 的 JavaType 构造泛型：SteamResponse<T>
        try {
            JavaType javaType = objectMapper.getTypeFactory()
                    .constructParametricType(SteamResponse.class, responseType);
            SteamResponse<T> steamResponse = objectMapper.readValue(response.getBody(), javaType);
            
            // 3. 返回内层的真实对象
            return steamResponse != null ? steamResponse.getResponse() : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize Steam API response", e);
        }
    }

    /**
     * 发送 Storefront API (商店) 请求
     * 适用于 https://store.steampowered.com/api/appdetails 这类非标准格式的接口。
     * 这类接口通常没有一层统一的 {"response": {...}} 外壳，因此直接反序列化为目标类型即可。
     *
     * @param apiPath      API 路径，例如 "api/appdetails"
     * @param queryParams  查询参数，例如 appids=440, l=schinese
     * @param responseType 希望解析到的目标实体类型，通常是一个 Map 或是特定的封装类
     */
    public <T> T getStoreApi(String apiPath, MultiValueMap<String, String> queryParams, Class<T> responseType) {
        if (queryParams == null) {
            queryParams = new LinkedMultiValueMap<>();
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getStoreBaseUrl())
                .path(apiPath.startsWith("/") ? apiPath : "/" + apiPath)
                .queryParams(queryParams)
                .build()
                .toUri();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            return objectMapper.readValue(response.getBody(), responseType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch or deserialize Steam Store API response", e);
        }
    }

    /**
     * 发送 Service 接口的请求 (POST 形式)
     * Steam 要求：名称以 "Service" 结尾的接口，参数作为单个 JSON 对象通过 input_json（URL 编码）传递，或者 protobuf 编码。
     * key/access_token/format 必须作为单独的查询参数或表单字段，不能放在 JSON 内。
     *
     * @param interfaceName 接口名称，如 "IWishlistService"
     * @param method        方法名称，如 "GetWishlistSortedFiltered"
     * @param version       版本，如 "v1"
     * @param inputJsonObj  将被序列化为 JSON 的 Java 对象
     * @param accessToken   如果该接口使用 access_token 鉴权（如商店特定接口），可传入
     * @param responseType  希望解析到的目标实体类型（即 {"response": ...} 内层的数据结构）
     */
    public <T> T postService(String interfaceName, String method, String version,
                             Object inputJsonObj, String accessToken, Class<T> responseType) {
        try {
            // 序列化为 JSON 字符串
            String jsonStr = objectMapper.writeValueAsString(inputJsonObj);

            // 构造 x-www-form-urlencoded 参数
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("input_json", jsonStr);
            if (accessToken != null && !accessToken.isEmpty()) {
                formData.add("access_token", accessToken);
            }

            URI uri = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .pathSegment(interfaceName, method, version, "")
                    .build()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

            // 1. 获取原始 JSON 字符串
            ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);

            // 2. 解析泛型
            JavaType javaType = objectMapper.getTypeFactory()
                    .constructParametricType(SteamResponse.class, responseType);
            SteamResponse<T> steamResponse = objectMapper.readValue(response.getBody(), javaType);

            return steamResponse != null ? steamResponse.getResponse() : null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Steam Service API request", e);
        }
    }
}
