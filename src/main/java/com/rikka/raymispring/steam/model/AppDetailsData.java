package com.rikka.raymispring.steam.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * appdetails 接口的内部数据结构 (对应 {"success": true, "data": {...}} 里的 data 字段)
 */
@Data
public class AppDetailsData {

    @JsonProperty("type")
    private String type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("steam_appid")
    private Integer steamAppId;

    @JsonProperty("required_age")
    private Integer requiredAge;

    @JsonProperty("is_free")
    private Boolean isFree;

    @JsonProperty("detailed_description")
    private String detailedDescription;

    @JsonProperty("about_the_game")
    private String aboutTheGame;

    @JsonProperty("short_description")
    private String shortDescription;

    @JsonProperty("supported_languages")
    private String supportedLanguages;

    @JsonProperty("header_image")
    private String headerImage;

    @JsonProperty("developers")
    private List<String> developers;

    @JsonProperty("publishers")
    private List<String> publishers;
}
