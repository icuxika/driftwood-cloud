package com.icuxika.dict;

import com.icuxika.annotation.SystemDict;

@SystemDict(name = "权限类型", type = 1, module = "用户")
public enum PermissionType {

    /**
     * 代表功能服务
     */
    SERVICE(1, "功能服务"),

    /**
     * 代表界面元素
     */
    UI(2, "界面元素");

    private final Integer code;

    private final String msg;

    PermissionType(Integer code, String msg) {
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
