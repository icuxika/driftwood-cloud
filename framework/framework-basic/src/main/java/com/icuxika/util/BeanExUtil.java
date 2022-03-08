package com.icuxika.util;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于Bean操作的一些扩展工具类
 *
 * @since 1.8
 */
public class BeanExUtil {

    /**
     * 字符串过滤策略
     */
    public enum StringIgnoreStrategy {
        /**
         * 不过滤
         */
        NULL,

        /**
         * 仅仅过滤为null的字符串
         */
        NOT_NULL,

        /**
         * 过滤为null或是空白的字符串
         */
        NOT_BLANK
    }

    /**
     * 默认忽略原始类型，仅仅过滤为null的字符串
     *
     * @param object 目标对象
     * @return 被忽略的属性名称数组
     */
    public static String[] getIgnorePropertyArray(Object object) {
        return getIgnorePropertyArray(object, StringIgnoreStrategy.NOT_NULL, true);
    }

    /**
     * 默认忽略原始类型
     *
     * @param object   目标对象
     * @param strategy 字符串过滤策略
     * @return 被忽略的属性名称数组
     */
    public static String[] getIgnorePropertyArray(Object object, StringIgnoreStrategy strategy) {
        return getIgnorePropertyArray(object, strategy, true);
    }

    /**
     * 过滤出Bean拷贝中应被忽略的属性名称，用于{@link org.springframework.beans.BeanUtils#copyProperties(Object, Object, String...)}
     *
     * @param object              目标对象
     * @param strategy            字符串过滤策略
     * @param ignorePrimitiveType 是否忽略原始类型，如int等
     * @return 被忽略的属性名称数组
     */
    public static String[] getIgnorePropertyArray(Object object, StringIgnoreStrategy strategy, boolean ignorePrimitiveType) {
        Set<String> ignorePropertySet = new HashSet<>();
        for (Field declaredField : object.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            if (declaredField.getType().isPrimitive()) {
                // 是否忽略原始类型
                if (ignorePrimitiveType) {
                    ignorePropertySet.add(declaredField.getName());
                }
            } else {
                try {
                    Object value = declaredField.get(object);
                    if (declaredField.getType().isAssignableFrom(String.class)) {
                        switch (strategy) {
                            case NULL:
                                break;
                            case NOT_NULL:
                                if (value == null) {
                                    ignorePropertySet.add(declaredField.getName());
                                }
                                break;
                            case NOT_BLANK:
                                String str = (String) value;
                                if (!StringUtils.hasText(str)) {
                                    ignorePropertySet.add(declaredField.getName());
                                    break;
                                }
                                break;
                        }
                    } else {
                        // 字符串以外的其他对象，值为null，则过滤
                        if (value == null) {
                            ignorePropertySet.add(declaredField.getName());
                        }
                    }
                } catch (IllegalAccessException e) {
                    // do nothing
                }
            }
        }
        return ignorePropertySet.toArray(new String[0]);
    }
}
