package com.icuxika.admin.vo;

import java.time.LocalDateTime;

public class OSSSignatureVO {

    /**
     * 用户请求的AccessKey ID
     */
    private String accessId;

    /**
     * 用户表单上传的策略（Policy），Policy为经过Base64编码过的字符串
     */
    private String policy;

    /**
     * 对Policy签名后的字符串
     */
    private String signature;

    /**
     * 限制上传的文件前缀
     */
    private String dir;

    /**
     * 用户发送上传请求的域名
     */
    private String host;

    /**
     * 由服务器端指定的Policy过期时间
     */
    private LocalDateTime expire;

    /**
     * 上传回调
     */
    private String callback;

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public LocalDateTime getExpire() {
        return expire;
    }

    public void setExpire(LocalDateTime expire) {
        this.expire = expire;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}
