package com.thread.threads_project.aop;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志切面处理<br>
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @AfterReturning(pointcut="@annotation(org.springframework.web.bind.annotation.RequestMapping)" +
            "||@annotation(org.springframework.web.bind.annotation.GetMapping)" +
            "||@annotation(org.springframework.web.bind.annotation.PostMapping)",returning="rvt")
    public Object doAround(JoinPoint pjp, Object rvt) throws Throwable {
        long startTimeMillis = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object[] args = pjp.getArgs();
        String requestPath = request.getRequestURI();
        Object result =rvt;
        long endTimeMillis = System.currentTimeMillis();
        try {
            log.info("\n"
                    + "Begin:" + "\n"
                    + "Url:" + requestPath + "\n"
                    + "RequestParams:" + JSONObject.toJSONString(args) + "\n"
                    + "ResponseResults:" + JSONObject.toJSONString(result) + "\n"
                    + "Cost:" + (endTimeMillis - startTimeMillis) + " ms " + "\n"
                    + "End" + "\n");
        }catch (Exception e){
            log.info("LogAspect 记录日志报错");
            e.printStackTrace();
        }
        return result;
    }

}
