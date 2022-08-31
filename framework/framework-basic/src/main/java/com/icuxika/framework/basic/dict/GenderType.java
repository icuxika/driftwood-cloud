package com.icuxika.framework.basic.dict;

import com.icuxika.framework.basic.annotation.SystemDict;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SystemDict(name = "性别类型")
public enum GenderType {

    UNKNOWN(1, "未知"),
    MALE(2, "男性"),
    FEMALE(3, "女性");
    private final Integer code;

    private final String msg;

    GenderType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static final Map<Integer, GenderType> MAP = Arrays.stream(values())
            .collect(Collectors.toMap(GenderType::getCode, Function.identity()));

    public static GenderType get(Integer code) {
        return MAP.get(code);
    }
}
