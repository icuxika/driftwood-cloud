package com.icuxika.authorization.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.authorization.config.jackson2.LongMixin;
import com.icuxika.authorization.config.jackson2.UserDetailsImplMixin;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;

@Configuration
public class SessionSerializerConfig implements BeanClassLoaderAware {

    private ClassLoader loader;

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
        mapper.addMixIn(UserDetailsImpl.class, UserDetailsImplMixin.class);
        mapper.addMixIn(Long.class, LongMixin.class);
        return mapper;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.loader = classLoader;
    }
}
