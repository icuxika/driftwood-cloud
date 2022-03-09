package org.flowable.ui.common.security;

import org.flowable.spring.boot.FlowableSecurityAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.ui.common.rest.idm.CurrentUserProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

import java.util.Arrays;

/**
 * 暂时无法解决<b>flowable-spring-boot-starter-ui-modeler</b>依赖的<b>flowable-spring-boot-starter-ui-idm</b>的
 * <b>Spring Security</b>与本项目一些配置冲突，只能使用<b>flowable-ui</b>的docker镜像并配合一些api来完成相应功能
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({
        IdmEngineServicesAutoConfiguration.class,
})
@AutoConfigureBefore({
        FlowableSecurityAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
})
public class FlowableUiSecurityAutoConfiguration {

    @Bean
    public ApiHttpSecurityCustomizer apiHttpSecurityCustomizer() {
        return http -> {

        };
    }

    @Bean
    public CurrentUserProvider currentUserProvider() {
        return new CurrentUserProvider() {
            @Override
            public UserRepresentation getCurrentUser(Authentication authentication) {
                UserRepresentation userRepresentation = new UserRepresentation();
                userRepresentation.setId("0");
                userRepresentation.setFirstName("driftwood");
                userRepresentation.setLastName("driftwood");
                userRepresentation.setPrivileges(
                        Arrays.asList(
                                DefaultPrivileges.ACCESS_IDM,
                                DefaultPrivileges.ACCESS_MODELER,
                                DefaultPrivileges.ACCESS_ADMIN,
                                DefaultPrivileges.ACCESS_TASK,
                                DefaultPrivileges.ACCESS_REST_API
                        )
                );
                return userRepresentation;
            }

            @Override
            public boolean supports(Authentication authentication) {
                return true;
            }
        };
    }
}
