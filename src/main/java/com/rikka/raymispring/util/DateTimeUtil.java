package com.rikka.raymispring.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author 晏波
 * 2025/12/18 9:44
 */
public class DateTimeUtil {
    /**
     * 将 Date 转换为 LocalDateTime
     * @param date Date 对象
     * @return LocalDateTime 对象
     */
    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将 LocalDateTime 转换为 Date
     * @param localDateTime LocalDateTime 对象
     * @return Date 对象
     */
    public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 秒级时间戳转化为LocalDate
     * @param timestamp 秒级时间戳
     * @return LocalDate对象
     */
    public static LocalDate timestampToLocalDate(long timestamp) {
        return Instant.ofEpochSecond(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * 秒级时间戳转化为LocalDateTime
     * @param timestamp 秒级时间戳
     * @return LocalDateTime对象
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return Instant.ofEpochSecond(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // Date转yyyy-MM-dd字符串
    public static String dateToString(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
    }
}
