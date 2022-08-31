package com.icuxika.framework.xxl.job.annotations;

import com.icuxika.framework.xxl.job.config.XxlJobAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(XxlJobAutoConfiguration.class)
public @interface EnableFrameworkXxlJob {
}
