package com.icuxika.framework.config.ext;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(ExtProperties.class)
public class ExtAutoConfiguration {
}
