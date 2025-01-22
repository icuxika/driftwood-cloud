package com.icuxika.framework.config.lock;

import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.config.annotation.RedissonLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class RedissonLockAspect {

    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("@annotation(com.icuxika.framework.config.annotation.RedissonLock)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) {
        Object object = null;
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        RedissonLock redissonLock = methodSignature.getMethod().getAnnotation(RedissonLock.class);
        RLock rLock = redissonClient.getLock(redissonLock.key());
        if (rLock != null) {
            boolean result = false;
            try {
                if (redissonLock.leaseTime() > 0) {
                    result = rLock.tryLock(redissonLock.waitTime(), redissonLock.leaseTime(), TimeUnit.MILLISECONDS);
                } else {
                    result = rLock.tryLock(redissonLock.waitTime(), TimeUnit.MILLISECONDS);
                }
                if (result) {
                    if (log.isInfoEnabled()) {
                        log.info("[{}]Redisson锁获取成功", redissonLock.name());
                    }
                    object = pjp.proceed();
                } else {
                    if (log.isErrorEnabled()) {
                        log.error("[{}]Redisson锁获取失败", redissonLock.name());
                    }
                    throw new GlobalServiceException(redissonLock.error());
                }
            } catch (Throwable e) {
                if (log.isErrorEnabled()) {
                    log.error("[{}]Redisson锁获取异常：{}", redissonLock.name(), e.getMessage());
                }
                throw new GlobalServiceException(redissonLock.error());
            } finally {
                if (result) {
                    rLock.unlock();
                }
            }
        }
        return object;
    }
}
