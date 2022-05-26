package com.icuxika.annotations;

import com.icuxika.config.FrameworkJpaAuditingRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用JPA审计功能，自动填充 createTime、createUserId、updateTime、updateUserId
 */
@Configuration
@EnableJpaAuditing
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FrameworkJpaAuditingRegistrar.class)
public @interface EnableFrameworkJpaAuditing {
}
