package com.icuxika.framework.oss;

import com.icuxika.framework.oss.core.FileProperties;
import com.icuxika.framework.oss.local.LocalAutoConfiguration;
import com.icuxika.framework.oss.remote.RemoteAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import({LocalAutoConfiguration.class, RemoteAutoConfiguration.class})
@EnableConfigurationProperties(FileProperties.class)
public class OssAutoConfiguration {
}
