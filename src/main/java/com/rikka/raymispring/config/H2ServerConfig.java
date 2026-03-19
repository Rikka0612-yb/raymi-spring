package com.rikka.raymispring.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.h2.tools.*;
import java.sql.SQLException;

/**
 * @author 晏波
 * 2026/3/19 21:21
 */
@Slf4j
@Configuration
public class H2ServerConfig {


    @Profile("dev") // 只在开发环境启用，生产环境请关闭
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

}
