package com.icuxika.framework.basic.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String getLocalDateTimeText() {
        return getLocalDateTimeText(LocalDateTime.now());
    }

    public static String getLocalDateTimeText(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
