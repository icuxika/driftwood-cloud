package com.icuxika.framework.basic.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

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
