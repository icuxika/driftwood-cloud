package com.icuxika.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class FrameworkResourceServerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(FrameworkResourceServerWebSecurityConfigurerAdapter.class);
        registry.registerBeanDefinition(FrameworkResourceServerWebSecurityConfigurerAdapter.class.getName(), genericBeanDefinition);
    }
}
