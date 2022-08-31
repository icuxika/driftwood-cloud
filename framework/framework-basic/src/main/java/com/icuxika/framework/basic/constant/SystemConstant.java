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
     * 存储用户不同设备当前激活的AccessToken
     */
    public final static String REDIS_OAUTH2_USER_SESSION = "DRIFTWOOD:OAUTH2:USER_SESSION";

    /**
     * 生成token时携带的信息：用户权限
     */
    public final static String OAUTH2_JWT_CLAIM_KEY_AUTHORITIES = "authorities";

    /**
     * 生成token时携带的信息：用户ID
     */
    public final static String OAUTH2_JWT_CLAIM_KEY_USER_ID = "userId";

    /**
     * 生成token时携带的信息：设备类型
     */
    public final static String OAUTH2_JWT_CLAIM_KEY_CLIENT_TYPE = "clientType";

    /**
     * MinIO 存储空间名称
     */
    public final static String MINIO_BUCKET_NAME = "driftwood";

    /**
     * 缓存用户登录时请求的验证码
     */
    public final static String REDIS_OAUTH2_PHONE_CODE = "DRIFTWOOD:OAUTH2:PHONE_CODE";
}
