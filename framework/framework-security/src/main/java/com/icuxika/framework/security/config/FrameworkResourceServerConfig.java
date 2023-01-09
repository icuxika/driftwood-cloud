package com.icuxika.framework.security.config;

import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.security.annotation.Anonymous;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FrameworkResourceServerConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        List<String> anonymousPathList = buildAnonymousPathList();
        httpSecurity
                .csrf().disable()
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        // flowable-ui -> modeler
                        .antMatchers("/actuator/**", "/druid/**", "/modeler/**", "/modeler-app/**").permitAll()
                        .antMatchers(anonymousPathList.toArray(new String[0])).permitAll()
                        .requestMatchers(new AuthorizeRequestMatcher()).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .authenticationEntryPoint(new AuthenticationEntryPointImpl())
                        .accessDeniedHandler(new AccessDeniedHandlerImpl())
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
        return httpSecurity.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(SystemConstant.OAUTH2_JWT_CLAIM_KEY_AUTHORITIES);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    /**
     * 允许匿名访问
     */
    private List<String> buildAnonymousPathList() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents("com.icuxika");
        List<String> anonymousPathList = new ArrayList<>();
        // 扫描所有的RestController中被@Anonymous注解的方法
        for (BeanDefinition beanDefinition : beanDefinitionSet) {
            if (beanDefinition instanceof ScannedGenericBeanDefinition scannedGenericBeanDefinition) {
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
        return anonymousPathList;
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
