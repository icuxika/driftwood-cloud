package com.icuxika.framework.basic.transfer.auth;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
public class QRCodeCache implements Serializable {

    @Setter
    private Integer status;

    @Setter
    private Long userId;

    private final Long createTime = System.currentTimeMillis();

    public QRCodeCache() {
    }

    public QRCodeCache(Integer status) {
        this.status = status;
    }
}
