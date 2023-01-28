package com.icuxika.framework.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
public class RedisCacheResolver {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 对于具有注解 @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) 的类，经过此序列化器存入Redis时会增加一个 @class 属性类记录类型信息，从而支持复杂数据类型的序列化与反序列化缓存
     * 对于Kotlin语言，应自定义ObjectMapper作为 GenericJackson2JsonRedisSerializer 构造参数，同时引入 jackson-module-kotlin
     */
    private final RedisSerializationContext.SerializationPair<Object> serializationPair = RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());

    public <T extends CacheData> void setCache(T value) {
        setCache(value.getCacheKey(), value);
    }

    public <T> void setCache(String key, T value) {
        ByteBuffer byteBuffer = serializationPair.write(value);
        byte[] bytes = ByteUtils.getBytes(byteBuffer);
        redisConnectionFactory.getConnection().set(key.getBytes(), bytes);
    }

    @SuppressWarnings("unchecked")
    public <T> T getCache(String key) {
        byte[] bytes = redisConnectionFactory.getConnection().get(key.getBytes());
        if (bytes == null) return null;
        return (T) serializationPair.read(ByteBuffer.wrap(bytes));
    }
}
