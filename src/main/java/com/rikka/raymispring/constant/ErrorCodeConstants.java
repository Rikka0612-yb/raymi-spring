package com.rikka.raymispring.constant;

import lombok.Getter;

/**
 * 自定义错误码
 */
@Getter
public enum ErrorCodeConstants {


    // 验证码相关错误
    CAPTCHA_ERROR(10010, "验证码错误"),
    CAPTCHA_REQUIRED(10011, "请输入验证码"),
    CAPTCHA_EXPIRED(10012, "验证码已过期"),
    IP_BLOCKED_ERROR(10020, "IP已被封禁"),
    SUCCESS(20000, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    USER_NOT_EXIST(40001, "用户不存在"),
    PARAM_ERROR(40105, "参数不合法"),
    PASSWORD_ERROR(40002, "密码错误"),
    LOGIN_CONCURRENT_ERROR(40003, "账号正在登录中"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    DATA_NOT_FOUND(50002, "数据不存在"),
    DUPLICATE_ERROR(50003, "数据重复"),
    EXISTS_ASSOCIATED_DATA(50004, "存在关联数据"),
    REDIS_CACHE_ERROR(50005, "Redis缓存异常"),
    FETCH_API_ERROR(50006, "获取接口数据异常"),
    RABBITMQ_ERROR(50007, "消息队列异常"),
    SECRET_KEY_ERROR(50008, "安全密钥错误"),
    DATA_QUALITY_ERROR(60001, "数据质量校验失败"),
    QUALITY_RULE_EXEC_ERROR(60002, "质控规则执行异常"),
    QUALITY_TASK_NOT_FOUND(60003, "质控任务不存在"),
    ;


    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCodeConstants(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
