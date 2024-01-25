package com.icuxika.framework.config.ext;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ext")
public class ExtProperties {

    /**
     * 根据指定的持久库来决定使用的Service实现
     */
    private String modelPersistenceType;

    /**
     * 测试使用
     */
    private String name = "test";

    public String getModelPersistenceType() {
        return modelPersistenceType;
    }

    public void setModelPersistenceType(String modelPersistenceType) {
        this.modelPersistenceType = modelPersistenceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
