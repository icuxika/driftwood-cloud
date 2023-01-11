package com.icuxika.framework.config.jpa.tenant;

import com.icuxika.framework.basic.constant.SystemConstant;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {

    // 暂时无用
    private String tenantId = SystemConstant.DEFAULT_TENANT_ID;

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        return tenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}
