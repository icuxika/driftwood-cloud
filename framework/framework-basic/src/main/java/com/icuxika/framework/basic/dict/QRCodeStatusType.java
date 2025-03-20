package com.icuxika.framework.basic.dict;

import com.icuxika.framework.basic.annotation.SystemDict;
import lombok.Getter;

@Getter
@SystemDict(name = "二维码状态")
public enum QRCodeStatusType {

    UN_SCANNED(1, "待扫描"),

    SCANNED(2, "已扫描"),

    CONFIRMED(3, "已确认"),

    EXPIRED(4, "已过期");

    private final Integer code;

    private final String msg;

    QRCodeStatusType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
