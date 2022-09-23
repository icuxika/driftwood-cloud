package com.icuxika.framework.oss.local;

import com.icuxika.framework.oss.core.FileProperties;
import com.icuxika.framework.oss.core.FileTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

public class LocalAutoConfiguration {

    private FileProperties fileProperties;

    @Bean
    @ConditionalOnMissingBean(LocalFileTemplate.class)
    @ConditionalOnProperty(name = "file.local.enable", havingValue = "true", matchIfMissing = true)
    public FileTemplate localFileTemplate() {
        return new LocalFileTemplate(fileProperties);
    }

    public LocalAutoConfiguration(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    public FileProperties getFileProperties() {
        return fileProperties;
    }

    public void setFileProperties(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }
}
