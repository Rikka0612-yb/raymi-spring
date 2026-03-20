package com.rikka.raymispring.steam.client;

import lombok.Data;

/**
 * 统一处理 Steam API 返回的外层结构 {"response": { ... }}
 *
 * @param <T> 内层实际数据的实体类型
 */
@Data
public class SteamResponse<T> {
    
    /**
     * Steam API 的外层包装节点
     * 绝大多数接口的真实数据都在这个节点内部
     */
    private T response;
    
}
