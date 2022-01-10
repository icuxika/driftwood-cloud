package com.icuxika.annotations;

import com.icuxika.config.FrameworkResourceServerRegistrar;
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
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FrameworkResourceServerRegistrar.class)
public @interface EnableFrameworkResourceServer {
}
