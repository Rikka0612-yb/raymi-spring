package com.rikka.raymispring.constant;

import lombok.Getter;

/**
 * 自定义错误码
 */
@Getter
public enum ErrorCodeConstants {


    // 验证码相关错误
    CAPTCHA_ERROR("A0101", "验证码错误"),
    CAPTCHA_REQUIRED("A0102", "请输入验证码"),
    CAPTCHA_EXPIRED("A0103", "验证码已过期"),
    IP_BLOCKED_ERROR("A0104", "IP已被封禁"),
    SUCCESS("00000", "ok"),
    PARAMS_ERROR("A0002", "请求参数错误"),
    USER_NOT_EXIST("A0003", "用户不存在"),
    PARAM_ERROR("A0004", "参数不合法"),
    PASSWORD_ERROR("A0005", "密码错误"),
    LOGIN_CONCURRENT_ERROR("A0006", "账号正在登录中"),
    NOT_LOGIN_ERROR("A0007", "未登录"),
    NO_AUTH_ERROR("A0008", "无权限"),
    NOT_FOUND_ERROR("A0009", "请求数据不存在"),
    FORBIDDEN_ERROR("A0010", "禁止访问"),
    SYSTEM_ERROR("B0001", "系统执行出错"),
    OPERATION_ERROR("B0002", "操作失败"),
    DATA_NOT_FOUND("B0003", "数据不存在"),
    DUPLICATE_ERROR("B0004", "数据重复"),
    EXISTS_ASSOCIATED_DATA("B0005", "存在关联数据"),
    REDIS_CACHE_ERROR("B0101", "Redis缓存异常"),
    FETCH_API_ERROR("C0001", "调用第三方服务出错"),
    RABBITMQ_ERROR("B0102", "消息队列异常"),
    SECRET_KEY_ERROR("B0103", "安全密钥错误"),
    DATA_QUALITY_ERROR("B0201", "数据质量校验失败"),
    QUALITY_RULE_EXEC_ERROR("B0202", "质控规则执行异常"),
    QUALITY_TASK_NOT_FOUND("B0203", "质控任务不存在"),
    ;


    /**
     * 状态码
     */
    private final String code;

    /**
     * 信息
     */
    private final String message;

    ErrorCodeConstants(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
