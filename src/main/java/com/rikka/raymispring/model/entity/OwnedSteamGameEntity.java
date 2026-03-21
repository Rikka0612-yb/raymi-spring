package com.rikka.raymispring.model.entity;

import com.rikka.raymispring.model.dto.steam.OwnedGamesResponse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 用户拥有的游戏实体类
 * 对应 Steam API 中的 GameInfo 属性
 */

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@IdClass(OwnedSteamGameEntity.PrimaryKey.class)
@Table(name = "owned_game", schema = "STEAM")
public class OwnedSteamGameEntity{

    /**
     * Steam 用户 ID (联合主键之一)
     */
    @Id
    @Column(name = "steamid", nullable = false, length = 64)
    private String steamid;

    /**
     * 游戏 App ID (联合主键之一)
     */
    @Id
    @Column(name = "app_id", nullable = false)
    private Integer appId;

    /**
     * 游戏名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 总游戏时间（分钟）
     */
    @Column(name = "playtime_forever")
    private Integer playtimeForever;

    /**
     * 游戏图标 URL 的 Hash
     */
    @Column(name = "img_icon_url")
    private String imgIconUrl;

    /**
     * 是否有社区可见的统计信息
     */
    @Column(name = "has_community_visible_stats")
    private Boolean hasCommunityVisibleStats;

    /**
     * 最后游玩时间 (Unix 时间戳)
     */
    @Column(name = "rtime_last_played")
    private Long rtimeLastPlayed;

    /**
     * Windows 平台总游戏时间（分钟）
     */
    @Column(name = "playtime_windows_forever")
    private Integer playtimeWindowsForever;

    /**
     * Mac 平台总游戏时间（分钟）
     */
    @Column(name = "playtime_mac_forever")
    private Integer playtimeMacForever;

    /**
     * Linux 平台总游戏时间（分钟）
     */
    @Column(name = "playtime_linux_forever")
    private Integer playtimeLinuxForever;

    /**
     * Steam Deck 平台总游戏时间（分钟）
     */
    @Column(name = "playtime_deck_forever")
    private Integer playtimeDeckForever;

    /**
     * 内容描述符 ID 列表
     * 将简单的 List<Integer> 映射到独立的集合表
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "owned_game_content_descriptor", schema = "STEAM",
            joinColumns = {
                    @JoinColumn(name = "steamid", referencedColumnName = "steamid"),
                    @JoinColumn(name = "app_id", referencedColumnName = "app_id")
            })
    @Column(name = "descriptor_id")
    private List<Integer> contentDescriptorIds;

    /**
     * 记录创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 记录更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrimaryKey implements Serializable {

        /**
         * Steam 用户 ID
         */
        private String steamid;

        /**
         * 游戏 App ID
         */
        private Integer appId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PrimaryKey that = (PrimaryKey) o;
            return Objects.equals(steamid, that.steamid) && Objects.equals(appId, that.appId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(steamid, appId);
        }
    }

    public OwnedSteamGameEntity(OwnedGamesResponse.GameInfo gameInfo,String steamid){
        this.setSteamid(steamid);
        this.setAppId(gameInfo.getAppId());
        this.setName(gameInfo.getName());
        this.setPlaytimeForever(gameInfo.getPlaytimeForever());
        this.setImgIconUrl(gameInfo.getImgIconUrl());
        this.setHasCommunityVisibleStats(gameInfo.getHasCommunityVisibleStats());
        this.setRtimeLastPlayed(gameInfo.getRtimeLastPlayed());
        this.setPlaytimeWindowsForever(gameInfo.getPlaytimeWindowsForever());
        this.setPlaytimeMacForever(gameInfo.getPlaytimeMacForever());
        this.setPlaytimeLinuxForever(gameInfo.getPlaytimeLinuxForever());
        this.setPlaytimeDeckForever(gameInfo.getPlaytimeDeckForever());
        this.setContentDescriptorIds(gameInfo.getContentDescriptorIds());
    }
}