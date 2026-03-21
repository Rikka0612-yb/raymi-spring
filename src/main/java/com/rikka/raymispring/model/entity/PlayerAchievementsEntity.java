package com.rikka.raymispring.model.entity;

import com.rikka.raymispring.model.dto.steam.PlayerAchievementsResponse;
import com.rikka.raymispring.util.DateTimeUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 晏波
 * 2026/3/22 3:30
 */

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@IdClass(PlayerAchievementsEntity.PrimaryKey.class)
@Table(name = "player_achievements", schema = "STEAM")
public class PlayerAchievementsEntity{

    @Id
    @Column(name = "steamid", nullable = false, length = 64)
    private String steamid;

    @Id
    @Column(name = "app_id", nullable = false)
    private Integer appid;

    @Id
    @Column(name = "api_name", nullable = false, length = 64)
    private String apiName;

    @Column(name = "achieved")
    private Integer achieved;

    @Column(name = "unlock_time")
    private Long unlockTime;

    @Column(name = "unlock_date")
    private LocalDateTime unlockDate;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "description", nullable = false, length = 64)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PrimaryKey implements Serializable {
        private String steamid;
        private Integer appid;
        private String apiName;
    }

    public PlayerAchievementsEntity(PlayerAchievementsResponse.Achievement achievement, String steamid, Integer appid){
        this.steamid = steamid;
        this.appid = appid;
        this.apiName = achievement.getApiName();
        this.achieved = achievement.getAchieved();
        this.unlockTime = achievement.getUnlockTime();
        this.unlockDate = achievement.getUnlockTime() != null && achievement.getUnlockTime() > 0 ?
                DateTimeUtil.timestampToLocalDateTime(achievement.getUnlockTime()) : null;
        this.name = achievement.getName();
        this.description = achievement.getDescription();
    }
}
