package com.github.messageconsumer.web.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect {

    private final HttpServletRequest request;

    @Before("execution(* com.github.messageconsumer..*Controller.*(..))")
    public void beforeAdvice() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String endpoint = request.getRequestURI(); // 현재 요청의 엔드포인트 경로를 얻어옴
            log.info("\u001B[34mAPI 호출! - " + endpoint + "\u001B[0m");
        } catch (IllegalStateException e) {
            log.info("\u001B[34mAPI 호출! - 요청 외부에서 로그가 호출되었습니다.\u001B[0m");
        }
    }

//    @AfterReturning(pointcut = "execution(* com.github.commerce..*Controller.*(..))", returning = "returnValue")
//    public void afterAdvice(JoinPoint joinPoint, Object returnValue) {
//        String endpoint = request.getRequestURI(); // 현재 요청의 엔드포인트 경로를 얻어옴
//
//        log.info("\u001B[32mAPI 호출 완료! - " + endpoint + " : "
//                + ((ResponseEntity<?>) returnValue).getBody() + "\u001B[0m");
//    }

}