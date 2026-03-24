package com.rikka.raymispring.service.impl;

import com.rikka.raymispring.constant.ErrorCodeConstants;
import com.rikka.raymispring.manager.SteamApiClient;
import com.rikka.raymispring.model.dto.steam.PlayerAchievementsResponse;
import com.rikka.raymispring.model.entity.OwnedSteamGameEntity;
import com.rikka.raymispring.model.entity.PlayerAchievementsEntity;
import com.rikka.raymispring.repository.OwnedGameRepository;
import com.rikka.raymispring.repository.PlayerAchievementsRepository;
import com.rikka.raymispring.service.ExceptionLogService;
import com.rikka.raymispring.service.PlayerAchievementsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 玩家成就同步实现:
 * 1) 从本地 owned_game 读取 appid 列表
 * 2) 逐个 appid 调 Steam 获取成就
 * 3) 覆盖式写入 player_achievements
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerAchievementsServiceImpl implements PlayerAchievementsService {

    private final SteamApiClient steamApiClient;
    private final OwnedGameRepository ownedGameRepository;
    private final PlayerAchievementsRepository playerAchievementsRepository;
    private final ExceptionLogService exceptionLogService;

    @Override
    public void syncAchievementsForOwnedGames(String steamid) {
        List<Integer> appIds = loadOwnedAppIds(steamid);
        if (appIds.isEmpty()) {
            log.warn("Skip syncing achievements because no owned games found, steamid={}", steamid);
            return;
        }

        log.info("Start syncing achievements for owned games, steamid={}, gameCount={}", steamid, appIds.size());
        int successCount = 0;
        int failedCount = 0;

        for (Integer appid : appIds) {
            try {
                syncAchievementsForGame(steamid, appid);
                successCount++;
            } catch (Exception ex) {
                if (isSkippableSteamError(ex)) {
                    log.warn("Skip game achievements sync due to expected Steam response, steamid={}, appid={}, reason={}",
                            steamid, appid, rootMessage(ex));
                    failedCount++;
                    asyncLogSyncFailure(steamid, appid, ex, true);
                    continue;
                }
                log.error("Failed to sync achievements for game, steamid={}, appid={}", steamid, appid, ex);
                failedCount++;
                asyncLogSyncFailure(steamid, appid, ex, false);
            }
        }

        log.info("Finished syncing achievements, steamid={}, success={}, failed={}", steamid, successCount, failedCount);
    }

    @Override
    public void syncAchievementsForGame(String steamid, Integer appid) {
        PlayerAchievementsResponse response = requestPlayerAchievements(steamid, appid);
        List<PlayerAchievementsEntity> entities = mapAchievementsToEntities(response, steamid, appid);

        if (!entities.isEmpty()) {
            playerAchievementsRepository.saveAll(entities);
        }

        log.info("Synced achievements for game, steamid={}, appid={}, achievementCount={}", steamid, appid, entities.size());
    }

    private List<Integer> loadOwnedAppIds(String steamid) {
        return ownedGameRepository.findBySteamid(steamid).stream()
                .map(OwnedSteamGameEntity::getAppId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private PlayerAchievementsResponse requestPlayerAchievements(String steamid, Integer appid) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("steamid", steamid);
        params.add("appid", String.valueOf(appid));
        params.add("l", "schinese");

        return steamApiClient.serviceGet(
                "ISteamUserStats",
                "GetPlayerAchievements",
                "v1",
                params,
                PlayerAchievementsResponse.class
        );
    }

    private List<PlayerAchievementsEntity> mapAchievementsToEntities(PlayerAchievementsResponse response, String steamid, Integer appid) {
        if (response == null
                || response.getPlayerStats() == null
                || !Boolean.TRUE.equals(response.getPlayerStats().getSuccess())
                || response.getPlayerStats().getAchievements() == null
                || response.getPlayerStats().getAchievements().isEmpty()) {
            return List.of();
        }

        return response.getPlayerStats().getAchievements().stream()
                .map(achievement -> new PlayerAchievementsEntity(achievement, steamid, appid))
                .collect(Collectors.toList());
    }

    private boolean isSkippableSteamError(Exception ex) {
        Throwable root = getRootCause(ex);
        if (root instanceof HttpClientErrorException httpError) {
            String body = httpError.getResponseBodyAsString();
            return httpError.getStatusCode().value() == 400
                    && body != null
                    && body.contains("Requested app has no stats");
        }

        String msg = rootMessage(ex);
        return msg.contains("HTTP 403") || msg.contains("HTTP 401");
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }

    private String rootMessage(Throwable throwable) {
        Throwable root = getRootCause(throwable);
        return root.getMessage() == null ? root.toString() : root.getMessage();
    }

    private void asyncLogSyncFailure(String steamid, Integer appid, Exception ex, boolean skippable) {
        Map<String, Object> sourceData = new HashMap<>();
        sourceData.put("steamid", steamid);
        sourceData.put("appid", appid);
        sourceData.put("language", "schinese");
        sourceData.put("api", "ISteamUserStats/GetPlayerAchievements/v1");
        sourceData.put("skippable", skippable);

        exceptionLogService.asyncLog(
                ErrorCodeConstants.FETCH_API_ERROR.getCode(),
                ErrorCodeConstants.FETCH_API_ERROR.getMessage(),
                rootMessage(ex),
                skippable ? "Sync player achievements skipped due to expected Steam response"
                        : "Sync player achievements failed",
                "steam-web-api",
                sourceData
        );
    }
}
