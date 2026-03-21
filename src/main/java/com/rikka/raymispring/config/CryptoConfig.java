package com.rikka.raymispring.config;

import com.rikka.raymispring.util.Aes256CryptoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 晏波
 * 2025/12/24 13:16
 */
@Slf4j
@Configuration
public class CryptoConfig {

    @Value("${encryption.aes.key:}")
    private String aesKey;

    /**
     * 如果配置文件中没有密钥，生成一个并打印到控制台
     */
    @Bean
    public String initAesKey() {
        if (aesKey == null || aesKey.trim().isEmpty()) {
            String generatedKey = Aes256CryptoUtil.generateRandomKey();
            log.info("""

                        ==========================================================
                        生成的AES密钥: {}
                        请将此密钥添加到 application.yml 中
                        encryption.aes.key:  {}
                        ==========================================================
                        """,generatedKey,generatedKey);
            return generatedKey;
        }
        return aesKey;
    }
}