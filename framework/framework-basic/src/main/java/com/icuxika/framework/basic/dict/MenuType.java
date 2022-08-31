package com.icuxika.framework.basic.dict;

import com.icuxika.framework.basic.annotation.SystemDict;

@SystemDict(name = "菜单类型", type = 1, module = "用户")
public enum MenuType {

    /**
     * 代表菜单
     */
    MENU(1, "菜单"),

    /**
     * 代表按钮
     */
    BUTTON(2, "按钮");

    private final Integer code;

    private final String msg;

    MenuType(Integer code, String msg) {
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
