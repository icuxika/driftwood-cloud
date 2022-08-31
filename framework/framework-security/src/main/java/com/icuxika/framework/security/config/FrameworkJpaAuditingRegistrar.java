package com.icuxika.framework.security.config;

import com.icuxika.framework.security.auditing.SpringSecurityAuditorAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class FrameworkJpaAuditingRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(SpringSecurityAuditorAware.class);
        registry.registerBeanDefinition(SpringSecurityAuditorAware.class.getName(), genericBeanDefinition);
    }
}
