package com.rikka.raymispring.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "steam.api")
public class SteamApiProperties {

    /**
     * Steam Web API 密钥
     */
    private String key;

    /**
     * 默认公共 API 域名
     */
    private String baseUrl = "https://api.steampowered.com";
//    private String baseUrl = "https://api.steamchina.com";

    /**
     * 合作伙伴专属高可用 API 域名 (需要 publisher key)
     */
    private String partnerBaseUrl = "https://partner.steam-api.com";

    /**
     * Steam 商店前端 API 域名 (Storefront API)
     * 用于 appdetails 等接口
     */
    private String storeBaseUrl = "https://store.steampowered.com";

    // 代理配置字段
    private String proxyHost;
    private Integer proxyPort;
}
