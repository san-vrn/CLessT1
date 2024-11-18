package org.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class TaskAspect {

    private static final Logger logger = LoggerFactory.getLogger(TaskAspect.class);

    @Before("@annotation(LogExecution)")
    public void logBefore(JoinPoint joinPoint) {

        logger.info("Start of request processing in the method: {}", joinPoint.getSignature().getName());
        logger.info("Method args: {}", Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "@annotation(LogExecution)", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        logger.info("End of request processing in the method: {}", joinPoint.getSignature().getName());
        logger.info(result.toString());
    }

    @Around("@annotation(LogExecution)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        logger.info("Method {} executed in {} ms", methodName, end - start);

        return result;
    }


    @AfterThrowing(pointcut = "@annotation(LogThrowing)", throwing = "throwable")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable throwable){
        logger.info("Exception throwing in method : {}", joinPoint.getSignature().getName());
        logger.error(throwable.toString());
    }

}
