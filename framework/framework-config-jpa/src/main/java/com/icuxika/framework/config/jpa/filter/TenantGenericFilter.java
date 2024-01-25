package com.icuxika.framework.config.jpa.filter;

import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.config.jpa.tenant.TenantIdentifierResolver;
import com.icuxika.framework.config.tenant.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantGenericFilter extends GenericFilterBean {

    private static final Logger L = LoggerFactory.getLogger(TenantGenericFilter.class);

    @Autowired
    private TenantIdentifierResolver tenantIdentifierResolver;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String tenantId = request.getHeader(SystemConstant.TENANT_ID_REQUEST_HEADER_KEY);
        if (tenantId == null) {
            tenantId = SystemConstant.DEFAULT_TENANT_ID;
        }
        tenantIdentifierResolver.setTenantId(tenantId);
        TenantContextHolder.setTenantId(tenantId);
        if (L.isInfoEnabled()) {
            L.info("当前租户：" + tenantId);
        }
        filterChain.doFilter(request, response);
        TenantContextHolder.clear();
    }
}
