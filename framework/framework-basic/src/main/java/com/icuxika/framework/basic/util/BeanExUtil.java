package com.icuxika.framework.basic.util;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

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
        List<Field> fieldList = new ArrayList<>();
        recursiveGetDeclaredFields(object.getClass(), fieldList);
        for (Field declaredField : fieldList) {
            declaredField.setAccessible(true);
            if (declaredField.getType().isPrimitive()) {
                // 是否忽略原始类型
                if (ignorePrimitiveType) {
                    ignorePropertySet.add(getFieldNameWithoutIs(declaredField.getName()));
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
                                    ignorePropertySet.add(getFieldNameWithoutIs(declaredField.getName()));
                                }
                                break;
                            case NOT_BLANK:
                                String str = (String) value;
                                if (!StringUtils.hasText(str)) {
                                    ignorePropertySet.add(getFieldNameWithoutIs(declaredField.getName()));
                                    break;
                                }
                                break;
                        }
                    } else {
                        // 字符串以外的其他对象，值为null，则过滤
                        if (value == null) {
                            ignorePropertySet.add(getFieldNameWithoutIs(declaredField.getName()));
                        }
                    }
                } catch (IllegalAccessException e) {
                    // do nothing
                }
            }
        }
        return ignorePropertySet.toArray(new String[0]);
    }

    /**
     * 递归的查询当前类及其继承的属性
     *
     * @param clazz     类
     * @param fieldList 属性集合
     */
    private static void recursiveGetDeclaredFields(Class<?> clazz, List<Field> fieldList) {
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (!clazz.getSuperclass().isAssignableFrom(Object.class)) {
            recursiveGetDeclaredFields(clazz.getSuperclass(), fieldList);
        }
    }

    /**
     * 对于属性名称如果以is开头则移除，针对于{@link org.springframework.beans.BeanUtils#copyProperties(Object, Object, String...)}获取类属性时自动移除is前缀
     *
     * @param fieldName 属性名称
     * @return is开头的属性名称经过转换后集合
     */
    private static String getFieldNameWithoutIs(String fieldName) {
        if (!fieldName.startsWith("is")) {
            return fieldName;
        }
        String fileNameWithoutIs = fieldName.substring(2);
        char[] charArray = fileNameWithoutIs.toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);
        return new String(charArray);
    }
}
