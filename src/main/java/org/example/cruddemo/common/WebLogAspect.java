package org.example.cruddemo.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * Web请求日志切面
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    /**
     * 定义切点：匹配 controller 包下所有类的所有方法
     */
    @Pointcut("execution(public * org.example.cruddemo.controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 环绕通知
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 开始时间
        long startTime = System.currentTimeMillis();

        // 2. 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.info("========================================== Start ==========================================");
            log.info("URL          : {}", request.getRequestURL().toString());
            log.info("HTTP Method  : {}", request.getMethod());
            log.info("IP           : {}", request.getRemoteAddr());
            log.info("Class Method : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            log.info("Request Args : {}", Arrays.toString(joinPoint.getArgs()));
        }

        // 3. 执行目标方法
        Object result = joinPoint.proceed();

        // 4. 计算耗时
        long timeConsumed = System.currentTimeMillis() - startTime;
        log.info("Response     : {}", result);
        log.info("Time Consumed: {} ms", timeConsumed);
        log.info("=========================================== End ===========================================");

        return result;
    }
}
