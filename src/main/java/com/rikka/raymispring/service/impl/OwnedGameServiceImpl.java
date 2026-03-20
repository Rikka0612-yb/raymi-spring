package com.rikka.raymispring.service.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rikka.raymispring.domain.OwnedGame;
import com.rikka.raymispring.domain.QOwnedGame;
import com.rikka.raymispring.repository.OwnedGameRepository;
import com.rikka.raymispring.service.OwnedGameService;
import com.rikka.raymispring.steam.client.SteamApiClient;
import com.rikka.raymispring.steam.model.OwnedGamesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnedGameServiceImpl implements OwnedGameService {

    private final SteamApiClient steamApiClient;
    private final OwnedGameRepository ownedGameRepository;
    // 注入 QueryDSL 查询工厂
    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncOwnedGames(String steamid) {
        log.info("Start syncing owned games for steamid: {}", steamid);

        // 1. 准备 Steam API 请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("steamid", steamid);
        params.add("include_appinfo", "true");
        params.add("include_played_free_games", "true");
        params.add("language", "schinese");

        // 2. 调用 GetOwnedGames API
        OwnedGamesResponse response = steamApiClient.get(
                "IPlayerService",
                "GetOwnedGames",
                "v1",
                params,
                OwnedGamesResponse.class
        );

        if (response == null || response.getGames() == null || response.getGames().isEmpty()) {
            log.warn("No games found for steamid: {} or response is null", steamid);
            return;
        }

        List<OwnedGamesResponse.GameInfo> games = response.getGames();
        log.info("Fetched {} games from Steam API for steamid: {}", games.size(), steamid);

        // 3. 将 DTO 转换为 Entity
        List<OwnedGame> ownedGames = new ArrayList<>();
        for (OwnedGamesResponse.GameInfo gameInfo : games) {
            OwnedGame ownedGame = new OwnedGame();
            ownedGame.setSteamid(steamid);
            ownedGame.setAppId(gameInfo.getAppId());
            ownedGame.setName(gameInfo.getName());
            ownedGame.setPlaytimeForever(gameInfo.getPlaytimeForever());
            ownedGame.setImgIconUrl(gameInfo.getImgIconUrl());
            ownedGame.setHasCommunityVisibleStats(gameInfo.getHasCommunityVisibleStats());
            ownedGame.setRtimeLastPlayed(gameInfo.getRtimeLastPlayed());
            ownedGame.setPlaytimeWindowsForever(gameInfo.getPlaytimeWindowsForever());
            ownedGame.setPlaytimeMacForever(gameInfo.getPlaytimeMacForever());
            ownedGame.setPlaytimeLinuxForever(gameInfo.getPlaytimeLinuxForever());
            ownedGame.setPlaytimeDeckForever(gameInfo.getPlaytimeDeckForever());
            ownedGame.setContentDescriptorIds(gameInfo.getContentDescriptorIds());
            
            ownedGames.add(ownedGame);
        }

        // 4. 批量保存到数据库（JpaRepository saveAll 会根据主键自动处理插入或更新）
        ownedGameRepository.saveAll(ownedGames);
        log.info("Successfully saved/updated {} games into database for steamid: {}", ownedGames.size(), steamid);
    }

    @Override
    public List<OwnedGame> getMostPlayedGames(String steamid, int minPlaytime) {
        // 使用生成的 Q 类
        QOwnedGame qOwnedGame = QOwnedGame.ownedGame;

        // 使用 JPAQueryFactory 进行流式查询
        return queryFactory.selectFrom(qOwnedGame)
                .where(
                        // 条件1: 匹配 steamid
                        qOwnedGame.steamid.eq(steamid),
                        // 条件2: 游玩时间大于指定值
                        qOwnedGame.playtimeForever.gt(minPlaytime)
                )
                // 按照游玩时间降序排列
                .orderBy(qOwnedGame.playtimeForever.desc())
                .fetch();
    }
}
