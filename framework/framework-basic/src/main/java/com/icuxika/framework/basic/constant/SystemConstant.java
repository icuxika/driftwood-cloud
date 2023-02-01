package com.icuxika.framework.basic.constant;

/**
 * 系统常量
 */
public class SystemConstant {

    /**
     * 系统创建数据时的创建者id
     */
    public static final Long SYSTEM_CREATE_USER_ID = 0L;

    /**
     * Tree数据根结点父id
     */
    public static final Long TREE_ROOT_ID = 0L;

    /**
     * Feign请求标识请求头key
     */
    public static final String FEIGN_REQUEST_HEADER_KEY = "feign-request";

    /**
     * Feign请求标识请求头value
     */
    public static final String FEIGN_REQUEST_HEADER_VALUE = "verification";

    /**
     * WebSocket请求连接建立需要在传递token的请求参数key
     */
    public static final String WEBSOCKET_QUERY_PARAMS_KEY = "token";

    /**
     * tenantId 请求头key
     */
    public static final String TENANT_ID_REQUEST_HEADER_KEY = "tenant-id";

    /**
     * 生成token时携带的信息：用户权限
     */
    public static final String OAUTH2_JWT_CLAIM_KEY_AUTHORITIES = "authorities";

    /**
     * 生成token时携带的信息：用户ID
     */
    public static final String OAUTH2_JWT_CLAIM_KEY_USER_ID = "userId";

    /**
     * 生成token时携带的信息：设备类型
     */
    public static final String OAUTH2_JWT_CLAIM_KEY_CLIENT_TYPE = "clientType";

    /**
     * MinIO 存储空间名称
     */
    public static final String MINIO_BUCKET_NAME = "driftwood";

    /**
     * 存储用户不同设备当前激活的AccessToken
     */
    public static final String REDIS_OAUTH2_USER_SESSION = "DRIFTWOOD:OAUTH2:USER_SESSION";

    /**
     * 缓存用户登录时请求的验证码
     */
    public static final String REDIS_OAUTH2_PHONE_CODE = "DRIFTWOOD:OAUTH2:PHONE_CODE";

    /**
     * 缓存用户第三方登录时获取到的openid
     */
    public static final String REDIS_OAUTH2_OPENID = "DRIFTWOOD:OAUTH2:OPENID";

    /**
     * 默认租户
     */
    public static final String DEFAULT_TENANT_ID = "driftwood";

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
}
