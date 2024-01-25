package com.icuxika.framework.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.icuxika.framework.security.util.SecurityUtil;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        fillValue(metaObject, "createTime", now);
        fillValue(metaObject, "createUserId", SecurityUtil.getUserId());
        fillValue(metaObject, "updateTime", now);
        fillValue(metaObject, "updateUserId", SecurityUtil.getUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fillValue(metaObject, "updateTime", LocalDateTime.now());
        fillValue(metaObject, "updateUserId", SecurityUtil.getUserId());
    }

    private void fillValue(MetaObject metaObject, String fieldName, Object fieldValue) {
        if (!metaObject.hasGetter(fieldName)) {
            return;
        }
        Object exist = metaObject.getValue(fieldName);
        Class<?> type = metaObject.getGetterType(fieldName);
        if (type.isAssignableFrom(fieldValue.getClass())) {
            metaObject.setValue(fieldName, fieldValue);
        }
    }
}
