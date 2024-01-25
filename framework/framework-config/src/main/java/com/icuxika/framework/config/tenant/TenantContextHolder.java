package com.icuxika.framework.config.tenant;

public class TenantContextHolder {

    private static final ThreadLocal<String> TENANT_ID_HOLDER = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        TENANT_ID_HOLDER.set(tenantId);
    }

    public static String getTenantId() {
        return TENANT_ID_HOLDER.get();
    }

    public static void clear() {
        TENANT_ID_HOLDER.remove();
    }
}
