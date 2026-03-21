package com.rikka.raymispring.service;

import com.rikka.raymispring.RaymiSpringApplication;
import com.rikka.raymispring.manager.SteamApiClient;
import com.rikka.raymispring.model.dto.steam.OwnedGamesResponse;
import com.rikka.raymispring.model.dto.steam.PlayerAchievementsResponse;
import com.rikka.raymispring.model.entity.PlayerAchievementsEntity;
import com.rikka.raymispring.repository.PlayerAchievementsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 晏波
 * 2026/3/20 23:20
 */
@Slf4j
@ActiveProfiles({"dev", "secret"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = RaymiSpringApplication.class)
public class SteamTest {

    @Autowired
    private SteamApiClient steamApiClient;

    @Test
    public void testGetOwnedGames() {
        // 1. 准备参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("steamid", "76561199466251834");
        params.add("include_appinfo", "true");
        params.add("include_played_free_games", "true");
        params.add("language", "schinese");


        // 2. 调用 API（注意最后一个参数直接传你定义的实体类即可）
        // SteamApiClient 内部会自动处理 {"response": {...}} 的外壳
        OwnedGamesResponse result = steamApiClient.serviceGet(
                "IPlayerService",
                "GetOwnedGames",
                "v1",
                params,
                OwnedGamesResponse.class
        );

// 3. 直接获取并使用解析好的复杂对象集合
        System.out.println("总游戏数：" + result.getResponse().getGameCount());
        for (OwnedGamesResponse.GameInfo game : result.getResponse().getGames()) {
            System.out.println("游戏名：" + game.getName());
            System.out.println("游玩时长：" + game.getPlaytimeForever());
        }
        log.info("response: {}", result);
    }

    @Test
    void test1(){
        String response = steamApiClient.serviceGet(
                "api",
                "appdetails",
                "v2",
                new LinkedMultiValueMap<>(),
                String.class
        );
    }

    public void testWishlistService() {
        // 构造请求体实体类或 Map
        Map<String, Object> input = new HashMap<>();
        input.put("steamid", "76561199466251834");

        // 调用以 Service 结尾的接口，自动封装为 input_json
        String response = steamApiClient.servicePost(
                "IWishlistService", "GetWishlist", "v1", input, null, String.class
        );
    }

    @Test
    public void testAppDetails() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("appids", "1091500");
        params.add("l", "schinese");
        params.add("cc", "CN");

        // 重点：使用 JsonNode.class 获取带有动态 Key 的原始 JSON 树
        com.fasterxml.jackson.databind.JsonNode rootNode =
                steamApiClient.pathGet("api/appdetails", params, com.fasterxml.jackson.databind.JsonNode.class);

        // 获取动态的 appId 节点 (比如 "440")
        com.fasterxml.jackson.databind.JsonNode appNode = rootNode.get("440");
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();


        log.info("response: {}", rootNode);
    }

    @Autowired
    private PlayerAchievementsRepository playerAchievementsRepository;
    @Test
    public void testAppDetails2() throws Exception {
        // 1. 准备参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("steamid", "76561199466251834");
        params.add("appid", "1091500");
        params.add("key", "40689EAEA555CA14A228798DB5FC2783");
        params.add("l", "schinese");


        // 2. 调用 API（注意最后一个参数直接传你定义的实体类即可）
        // SteamApiClient 内部会自动处理 {"response": {...}} 的外壳
        PlayerAchievementsResponse result = steamApiClient.serviceGet(
                "ISteamUserStats",
                "GetPlayerAchievements",
                "v1",
                params,
                PlayerAchievementsResponse.class
        );
        log.info("response: {}", result);
        result.getPlayerStats().getAchievements().forEach(achievement -> {
            PlayerAchievementsEntity entity =
                    new PlayerAchievementsEntity(achievement, result.getPlayerStats().getSteamID(), 1091500);
            playerAchievementsRepository.save(entity);
        });
    }






}
