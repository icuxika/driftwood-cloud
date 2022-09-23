package com.icuxika.framework.oss.remote;

import com.icuxika.framework.oss.core.FileProperties;
import com.icuxika.framework.oss.core.FileTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class RemoteAutoConfiguration {

    private FileProperties fileProperties;

    @Bean
    @Primary
    @ConditionalOnMissingBean(RemoteFileTemplate.class)
    @ConditionalOnProperty(name = "file.remote.enable", havingValue = "true")
    public FileTemplate remoteFileTemplate() {
        return new RemoteFileTemplate(fileProperties);
    }

    public RemoteAutoConfiguration(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    public FileProperties getFileProperties() {
        return fileProperties;
    }

    public void setFileProperties(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }
}
