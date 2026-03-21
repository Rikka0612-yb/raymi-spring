package com.rikka.raymispring.service;

import com.rikka.raymispring.model.entity.OwnedSteamGameEntity;

import java.util.List;

/**
 * 用户拥有的游戏业务服务接口
 */
public interface OwnedGameService {

    /**
     * 同步指定 Steam 用户的拥有游戏数据
     * 从 Steam API 拉取数据并保存/更新到本地数据库
     *
     * @param steamid Steam 用户 ID
     */
    void syncOwnedGames(String steamid);

    /**
     * 【QueryDSL 示例】查询某个用户游玩时间超过指定分钟数的游戏，并按游玩时间倒序排列
     *
     * @param steamid Steam 用户 ID
     * @param minPlaytime 最小游玩时间（分钟）
     * @return 游戏列表
     */
    List<OwnedSteamGameEntity> getMostPlayedGames(String steamid, int minPlaytime);
}
