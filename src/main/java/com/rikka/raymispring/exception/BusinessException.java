package com.rikka.raymispring.exception;


import com.rikka.raymispring.constant.ErrorCodeConstants;
import lombok.Getter;

/**
 * 自定义异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCodeConstants errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCodeConstants errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}
