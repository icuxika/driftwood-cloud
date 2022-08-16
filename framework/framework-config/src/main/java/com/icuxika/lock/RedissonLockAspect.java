package com.icuxika.lock;

import com.icuxika.annotation.RedissonLock;
import com.icuxika.exception.GlobalServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class RedissonLockAspect {

    private static final Logger L = LoggerFactory.getLogger(RedissonLockAspect.class);

    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("@annotation(com.icuxika.annotation.RedissonLock)")
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
                if (redissonLock.lockUntil() > 0) {
                    result = rLock.tryLock(redissonLock.lockUntil(), redissonLock.expireIn(), TimeUnit.MILLISECONDS);
                } else {
                    result = rLock.tryLock(redissonLock.expireIn(), TimeUnit.MILLISECONDS);
                }
                if (result) {
                    L.info("[" + redissonLock.name() + "]锁获取成功");
                    object = pjp.proceed();
                } else {
                    L.error("[" + redissonLock.name() + "]锁获取失败");
                    throw new GlobalServiceException(redissonLock.error());
                }
            } catch (Throwable e) {
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
