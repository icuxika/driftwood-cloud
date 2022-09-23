package com.icuxika.framework.oss.remote;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "remote")
public class RemoteProperties {
    private Boolean enable;
    private String endpoint;
    private String accessKey;
    private String secretKey;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
