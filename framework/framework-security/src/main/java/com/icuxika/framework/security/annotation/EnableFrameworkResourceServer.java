package com.icuxika.framework.security.annotation;

import com.icuxika.framework.security.config.FrameworkResourceServerConfig;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用授权服务器
 */
@EnableWebSecurity
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(FrameworkResourceServerConfig.class)
public @interface EnableFrameworkResourceServer {
}
