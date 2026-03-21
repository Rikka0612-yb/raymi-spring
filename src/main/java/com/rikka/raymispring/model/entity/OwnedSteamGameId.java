package com.rikka.raymispring.model.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

/**
 * 用户拥有的游戏联合主键
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnedSteamGameId implements Serializable {

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
        OwnedSteamGameId that = (OwnedSteamGameId) o;
        return Objects.equals(steamid, that.steamid) && Objects.equals(appId, that.appId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(steamid, appId);
    }
}
