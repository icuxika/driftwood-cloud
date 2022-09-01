package com.icuxika.framework.basic.dict;

import com.icuxika.framework.basic.annotation.SystemDict;

@SystemDict(name = "菜单类型", type = 1, module = "用户")
public enum MenuType {

    /**
     * 代表菜单
     */
    MENU_APPLICATION(1, "菜单-应用"),
    MENU_SERVICE(2, "菜单-服务"),
    MENU_URL(3, "菜单-URL"),

    /**
     * 代表按钮
     */
    BUTTON(4, "按钮");

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
