package com.icuxika.enumerate;

/**
 * 支持的授权类型
 */
public enum SupportGrantType {
    PASSWORD("password", "密码"),
    PHONE("phone", "短信登录");
    private final String type;

    private final String description;

    SupportGrantType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
