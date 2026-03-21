package com.rikka.raymispring.model.dto.steam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * GetOwnedGames 接口返回数据的实体映射示例
 * 对应 {"response": {...这个类的结构...}}
 */
@Data
public class OwnedGamesResponse {

    @JsonProperty("game_count")
    private Integer gameCount;

    @JsonProperty("games")
    private List<GameInfo> games;

    @Data
    public static class GameInfo {
        @JsonProperty("appid")
        private Integer appId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("playtime_forever")
        private Integer playtimeForever;

        @JsonProperty("img_icon_url")
        private String imgIconUrl;

        @JsonProperty("has_community_visible_stats")
        private Boolean hasCommunityVisibleStats;

        @JsonProperty("rtime_last_played")
        private Long rtimeLastPlayed;
        
        @JsonProperty("playtime_windows_forever")
        private Integer playtimeWindowsForever;
        
        @JsonProperty("playtime_mac_forever")
        private Integer playtimeMacForever;
        
        @JsonProperty("playtime_linux_forever")
        private Integer playtimeLinuxForever;
        
        @JsonProperty("playtime_deck_forever")
        private Integer playtimeDeckForever;

        @JsonProperty("content_descriptorids")
        private List<Integer> contentDescriptorIds;
    }
}
