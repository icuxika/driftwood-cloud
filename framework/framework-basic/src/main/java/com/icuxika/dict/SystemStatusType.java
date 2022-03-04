package com.icuxika.dict;

import com.icuxika.annotation.SystemDict;

@SystemDict(name = "数据状态")
public enum SystemStatusType {

    /**
     * 正常
     */
    NORMAL(1, "正常"),

    /**
     * 删除
     */
    DELETE(2, "删除"),

    /**
     * 禁用
     */
    DISABLE(3, "禁用");

    private final Integer code;

    private final String msg;

    SystemStatusType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
