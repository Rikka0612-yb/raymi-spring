package com.rikka.raymispring.model.dto.steam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 晏波
 * 2026/3/22 3:22
 */
@Data
public class PlayerAchievementsResponse {

    @JsonProperty("playerstats")
    private PlayerAchievementsBody playerStats;

    @Data
    public static class PlayerAchievementsBody {
        @JsonProperty("steamID")
        private String steamID;

        @JsonProperty("gameName")
        private String gameName;

        @JsonProperty("achievements")
        private List<Achievement> achievements;

        @JsonProperty("success")
        private Boolean success;
    }

    @Data
    public static class Achievement {
        @JsonProperty("apiname")
        private String apiName;

        @JsonProperty("achieved")
        private Integer achieved;

        @JsonProperty("unlocktime")
        private Long unlockTime;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;
    }
}
