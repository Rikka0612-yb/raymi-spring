package com.rikka.raymispring.service;

import cn.hutool.core.lang.Assert;
import com.rikka.raymispring.RaymiSpringApplication;
import com.rikka.raymispring.model.entity.OwnedSteamGameEntity;
import com.rikka.raymispring.repository.OwnedGameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles({"dev", "secret"})
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = RaymiSpringApplication.class)
class PlayerAchievementsServiceImplTest {

    @Autowired
    private PlayerAchievementsService playerAchievementsService;

    @Autowired
    private OwnedGameRepository ownedGameRepository;



    @Test
    void syncAchievementsForGame_shouldSyncAchievementsForGame() {
        playerAchievementsService.syncAchievementsForOwnedGames("76561199466251834");
    }


    @Test
    void syncAchievementsForOwnedGames_shouldSyncAchievementsForOwnedGames() {
        List<OwnedSteamGameEntity> all = ownedGameRepository.findAll();
        Assert.notEmpty(all);

        List<OwnedSteamGameEntity> bySteamid = ownedGameRepository.findBySteamid("76561199466251834");
        Assert.notEmpty(bySteamid);
    }
}
