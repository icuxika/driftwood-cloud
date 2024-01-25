package com.icuxika.framework.config.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.framework.basic.transfer.auth.PhoneCodeCache;
import org.springframework.boot.json.JsonParseException;

import java.util.List;
import java.util.concurrent.Callable;

public class JacksonUtil {

    private JacksonUtil() {

    }

    // 静态代码块单例
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static <T> T tryParse(Callable<T> parser) {
        return tryParse(parser, JacksonException.class);
    }

    public static <T> T tryParse(Callable<T> parser, Class<? extends Exception> check) {
        try {
            return parser.call();
        } catch (Exception e) {
            if (check.isAssignableFrom(e.getClass())) {
                throw new JsonParseException(e);
            }
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        PhoneCodeCache phoneCodeCache = JacksonUtil.tryParse(() -> JacksonUtil.getObjectMapper().readValue("", PhoneCodeCache.class));
        List<PhoneCodeCache> phoneCodeCacheList = JacksonUtil.tryParse(() -> JacksonUtil.getObjectMapper().readValue("", new TypeReference<>() {
        }));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(null);
        JacksonUtil.tryParse(() -> objectMapper.writeValueAsString(new PhoneCodeCache()));
    }
}
