package com.rikka.raymispring.tool;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rikka.raymispring.constant.CommonConstants;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 晏波
 * 2026/3/25 20:42
 */
public class WebSearchTool {

    private final String searchApiBaseUrl;
    private final String searchApiKey;
    private final String proxyHost;
    private final Integer proxyPort;

    public WebSearchTool(String searchApiBaseUrl, String searchApiKey) {
        this(searchApiBaseUrl, searchApiKey, CommonConstants.LOCAL_HOST, null);
    }

    public WebSearchTool(String searchApiBaseUrl, String searchApiKey, String proxyHost, Integer proxyPort) {
        this.searchApiBaseUrl = searchApiBaseUrl;
        this.searchApiKey = searchApiKey;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    @Tool(description = "Use web search tools if the conversation mentions something you don’t understand", name = "Raymi的浏览器")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", searchApiKey);
        try {
            // 创建 HTTP 请求
            HttpRequest request = HttpRequest.get(searchApiBaseUrl)
                    .form(paramMap);
            // 设置代理
            Proxy proxy = resolveProxy();
            if (proxy != null) {
                request.setProxy(proxy);
            }
            String response = request.execute().body();
            // 取出返回结果的前 5 条
            JSONObject jsonObject = JSONUtil.parseObj(response);
            // 提取 organic_results 部分
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            List<Object> objects = organicResults.subList(0, Math.min(organicResults.size(), 5));
            // 拼接搜索结果为字符串
            return objects.stream().map(obj -> {
                JSONObject tmpJSONObject = (JSONObject) obj;
                return tmpJSONObject.toString();
            }).collect(Collectors.joining(","));
        } catch (Exception e) {
            return "Error searching Baidu: " + e.getMessage();
        }
    }

    /**
     * 代理识别逻辑，参考 SteamRestTemplateConfig
     */
    private Proxy resolveProxy() {
        // 1. 优先从构造函数传入的配置中读取
        if (proxyHost != null && proxyPort != null) {
            return new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxyHost, proxyPort));
        }

        // 2. 自动探测常见的本地代理端口 (Clash/V2Ray/WattToolkit)
        int[] commonPorts = {7890, 10809, 1080, 10808};
        for (int port : commonPorts) {
            if (isLocalPortActive(port)) {
                // 发现可用端口，自动返回
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