package com.rikka.raymispring.common;


import com.rikka.raymispring.constant.ErrorCodeConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 通用返回类
 */
@Data
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String msg;

    public BaseResponse(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCodeConstants errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
