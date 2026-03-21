package com.rikka.raymispring.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author 晏波
 * 2025/11/8 8:58
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> implements Serializable {
    private List<T> records;
    private long total;
    private long size;
    private long current;
}
