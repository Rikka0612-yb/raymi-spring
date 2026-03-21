package com.rikka.raymispring.service;

import com.rikka.raymispring.RaymiSpringApplication;
import com.rikka.raymispring.domain.OwnedGame;
import com.rikka.raymispring.repository.OwnedGameRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ActiveProfiles({"dev", "secret"})
@SpringBootTest(classes = RaymiSpringApplication.class)
public class OwnedGameServiceTest {

    @Autowired
    private OwnedGameService ownedGameService;

    @Autowired
    private OwnedGameRepository ownedGameRepository;

    @Test
    public void testSyncOwnedGames() {
//        // 使用一个已知的 Steam ID，此 ID 在其他测试中使用过
        String testSteamId = "76561199466251834";

        // 1. 执行同步
        log.info("Starting testSyncOwnedGames...");
        ownedGameService.syncOwnedGames(testSteamId);

        // 2. 从数据库查询并验证
        List<OwnedGame> games = ownedGameRepository.findAll();
        log.info("Total games in database after sync: {}", games.size());

        assertNotNull(games);
        assertFalse(games.isEmpty(), "The games list should not be empty after sync");
//
        // 3. 测试 QueryDSL 方法
        int minPlaytime = 1000; // 查找游玩时间超过 1000 分钟的游戏
        List<OwnedGame> mostPlayedGames = ownedGameService.getMostPlayedGames(testSteamId, minPlaytime);
        
        log.info("--- QueryDSL Test ---");
        log.info("Found {} games played for more than {} minutes", mostPlayedGames.size(), minPlaytime);
        
        for (OwnedGame game : mostPlayedGames) {
            log.info("Game: {}, Playtime: {} minutes", game.getName(), game.getPlaytimeForever());
            assertTrue(game.getPlaytimeForever() > minPlaytime, "Playtime should be greater than " + minPlaytime);
        }
    }
}
