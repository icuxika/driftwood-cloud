package com.icuxika.framework.security.config;

import com.icuxika.framework.security.annotation.Anonymous;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FrameworkResourceServerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents("com.icuxika");
        List<String> anonymousPathList = new ArrayList<>();
        // 扫描所有的RestController中被@Anonymous注解的方法
        for (BeanDefinition beanDefinition : beanDefinitionSet) {
            if (beanDefinition instanceof ScannedGenericBeanDefinition) {
                ScannedGenericBeanDefinition scannedGenericBeanDefinition = (ScannedGenericBeanDefinition) beanDefinition;
                try {
                    Class<?> clazz = getClass().getClassLoader().loadClass(scannedGenericBeanDefinition.getBeanClassName());
                    boolean isRequestMappingOnClass = clazz.isAnnotationPresent(RequestMapping.class);
                    String basePath = "";
                    if (isRequestMappingOnClass) {
                        RequestMapping classRequestMapping = clazz.getAnnotation(RequestMapping.class);
                        String[] value = classRequestMapping.value();
                        if (value.length == 0) {
                            value = classRequestMapping.path();
                        }
                        if (value.length > 0) {
                            basePath = formatPath(value[0]);
                        }
                    }
                    String finalBasePath = basePath;
                    Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(Anonymous.class)).forEach(method -> {
                        String methodPath = "";
                        boolean foreachCheck = true;
                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            foreachCheck = false;
                            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                            String[] requestValue = requestMapping.value();
                            if (requestValue.length == 0) {
                                requestValue = requestMapping.path();
                            }
                            if (requestValue.length > 0) {
                                methodPath = formatPath(getMethodPath(requestValue[0]));
                            }
                        }
                        if (foreachCheck && method.isAnnotationPresent(GetMapping.class)) {
                            foreachCheck = false;
                            GetMapping getMapping = method.getAnnotation(GetMapping.class);
                            String[] requestValue = getMapping.value();
                            if (requestValue.length == 0) {
                                requestValue = getMapping.path();
                            }
                            if (requestValue.length > 0) {
                                methodPath = formatPath(getMethodPath(requestValue[0]));
                            }
                        }
                        if (foreachCheck && method.isAnnotationPresent(PostMapping.class)) {
                            foreachCheck = false;
                            PostMapping postMapping = method.getAnnotation(PostMapping.class);
                            String[] requestValue = postMapping.value();
                            if (requestValue.length == 0) {
                                requestValue = postMapping.path();
                            }
                            if (requestValue.length > 0) {
                                methodPath = formatPath(getMethodPath(requestValue[0]));
                            }
                        }
                        if (foreachCheck && method.isAnnotationPresent(PutMapping.class)) {
                            foreachCheck = false;
                            PutMapping putMapping = method.getAnnotation(PutMapping.class);
                            String[] requestValue = putMapping.value();
                            if (requestValue.length == 0) {
                                requestValue = putMapping.path();
                            }
                            if (requestValue.length > 0) {
                                methodPath = formatPath(getMethodPath(requestValue[0]));
                            }
                        }
                        if (foreachCheck && method.isAnnotationPresent(DeleteMapping.class)) {
                            foreachCheck = false;
                            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
                            String[] requestValue = deleteMapping.value();
                            if (requestValue.length == 0) {
                                requestValue = deleteMapping.path();
                            }
                            if (requestValue.length > 0) {
                                methodPath = formatPath(getMethodPath(requestValue[0]));
                            }
                        }
                        if (foreachCheck && method.isAnnotationPresent(PatchMapping.class)) {
                            PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
                            String[] requestValue = patchMapping.value();
                            if (requestValue.length == 0) {
                                requestValue = patchMapping.path();
                            }
                            if (requestValue.length > 0) {
                                methodPath = formatPath(getMethodPath(requestValue[0]));
                            }
                        }
                        anonymousPathList.add(finalBasePath + methodPath);
                    });
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addIndexedArgumentValue(0, anonymousPathList);

        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(FrameworkResourceServerWebSecurityConfigurerAdapter.class);
        genericBeanDefinition.setConstructorArgumentValues(constructorArgumentValues);
        registry.registerBeanDefinition(FrameworkResourceServerWebSecurityConfigurerAdapter.class.getName(), genericBeanDefinition);
    }

    /**
     * 获取方法上的请求路径
     *
     * @param value /a/b/c /a/{b}/c/{d}
     * @return /a/b/c /a/xx/c/xx
     */
    private String getMethodPath(String value) {
        if (value.contains("{")) {
            return value.replaceAll("\\{[^\\}]*\\}", "**");
        }
        return value;
    }

    private String formatPath(String path) {
        String result = path;
        if (!path.startsWith("/")) {
            result = "/" + path;
        }
        if (path.endsWith("/")) {
            result = path.substring(0, path.length() - 1);
        }
        return result;
    }
}
