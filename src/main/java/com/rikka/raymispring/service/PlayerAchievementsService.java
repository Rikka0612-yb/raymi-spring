package com.rikka.raymispring.service;

/**
 * 玩家成就同步服务
 */
public interface PlayerAchievementsService {

    /**
     * 根据本地已同步的游戏库(appid 列表)，批量同步指定用户的所有游戏成就。
     *
     * @param steamid Steam 用户 ID
     */
    void syncAchievementsForOwnedGames(String steamid);

    /**
     * 同步指定用户在单个游戏下的全部成就。
     *
     * @param steamid Steam 用户 ID
     * @param appid   游戏 appid
     */
    void syncAchievementsForGame(String steamid, Integer appid);
}
