package com.capitalbio.common.aspect;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalbio.common.aspect.annotation.CacheLock;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

/*
 * @Description TODO
 * @Author muruan.lt
 * @Date 2019/9/26 11:36
 */
@Aspect
@Slf4j
@Component
public class CacheLockAspect {
    private static final String LOCK_VALUE = "locked";
 
//    @Autowired
//    RedisCache redisCache;
    
    JedisTemplate template = RedisUtils.getTemplate();
 
    /*
     * 1.有springboot-websocket情况下不用@Synchronized这个注解，可能多线程导致的分布式锁不生效
     * 2.没有springboot-websocket情况下可以不用@Synchronized这个注解
     */
    @Around("@annotation(com.capitalbio.common.aspect.annotation.CacheLock)")
    @Synchronized
    public void cacheLockPoint(ProceedingJoinPoint pjp) {
 
        String name = pjp.getSignature().getName();
        Method[] methods = pjp.getTarget().getClass().getMethods();
        for (Method cacheMethod : methods) {
            if (null != cacheMethod.getAnnotation(CacheLock.class)
                    && name.equals(cacheMethod.getName())){
                try {
//                    String lockKey = pjp.getTarget().getClass().getName()+cacheMethod.getAnnotation(CacheLock.class).lockedPrefix();
                    String lockKey = cacheMethod.getAnnotation(CacheLock.class).lockedPrefix();
                    int timeOut = cacheMethod.getAnnotation(CacheLock.class).expireTime();
                    if(null == lockKey){
                        return;
                    }
                    if (!template.exists(lockKey)) {
                    	template.setex(lockKey, LOCK_VALUE, timeOut);
                    	pjp.proceed();
                    	return;
                    } else {
                    	log.debug("任务已存在，不再重复执行");
                    }
                } catch (Throwable e) {
                    log.error("method:{},运行错误！",cacheMethod,e);
                    return;
                }
                break;
            }
        }
    }
}