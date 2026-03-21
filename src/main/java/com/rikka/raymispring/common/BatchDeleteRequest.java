package com.rikka.raymispring.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author 晏波
 * 2025/11/7 10:30
 */
@Data
public class BatchDeleteRequest implements Serializable {

    /**
     * ids
     */
    private List<Long> ids;

    @Serial
    private static final long serialVersionUID = 1L;
}
