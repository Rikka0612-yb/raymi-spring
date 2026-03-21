package com.rikka.raymispring.model.dto.steam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * appdetails 接口对应某个 appId 的外壳包装
 * 对应 {"440": {"success": true, "data": {...}}} 中的 {"success": true, "data": {...}}
 */
@Data
public class AppDetailsResponse {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("data")
    private AppDetailsData data;
}
