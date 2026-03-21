package com.rikka.raymispring.common;


import com.rikka.raymispring.common.BaseResponse;
import com.rikka.raymispring.constant.ErrorCodeConstants;

/**
 * 返回工具类
 */
public class ResultUtils {

    /**
     * 成功
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ErrorCodeConstants.SUCCESS.getCode(), data, "ok");
    }

    public static <T> BaseResponse<T> success(T data,String  message) {
        return new BaseResponse<>(ErrorCodeConstants.SUCCESS.getCode(), data, message);
    }

    public static <T> BaseResponse<T> success(String  message) {
        return new BaseResponse<>(ErrorCodeConstants.SUCCESS.getCode(),null, message);
    }
    /**
     * 失败
     */
    public static<T> BaseResponse<T> error(ErrorCodeConstants errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     */
    public static<T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 失败
     */
    public static<T> BaseResponse<T> error(ErrorCodeConstants errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
