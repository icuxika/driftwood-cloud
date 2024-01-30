package com.icuxika.framework.basic.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String DEFAULT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN);

    /**
     * 根据时间戳获取系统默认 LocalDateTime 对象
     *
     * @param timeMillis 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(long timeMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
    }

    /**
     * 根据时间戳和指定时区获取 LocalDateTime 对象
     *
     * @param timeMillis 时间戳
     * @param zoneId     时区
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(long timeMillis, ZoneId zoneId) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), zoneId);
    }

    /**
     * 根据时间戳和指定时区获取 LocalDateTime 对象
     *
     * @param timeMillis 时间戳
     * @param zoneOffset 时区
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(long timeMillis, ZoneOffset zoneOffset) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), zoneOffset);
    }


    public static String getLocalDateTimeText() {
        return getLocalDateTimeText(LocalDateTime.now());
    }

    public static String getLocalDateTimeText(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    public static String getLocalDateText() {
        return getLocalDateText(LocalDate.now());
    }

    public static String getLocalDateText(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String getLocalTimeText() {
        return getLocalTimeText(LocalTime.now());
    }

    public static String getLocalTimeText(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("HHmmss"));
    }
}
