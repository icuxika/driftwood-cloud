package com.icuxika.framework.basic.dict;

public enum OpenAuthType {

    GITHUB(1, "GitHub"),
    GITEE(2, "Gitee"),
    WECHAT_MINI(3, "微信小程序"),
    ALIPAY_MINI(4, "支付宝小程序");

    private final Integer code;

    private final String msg;

    OpenAuthType(Integer code, String msg) {
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
