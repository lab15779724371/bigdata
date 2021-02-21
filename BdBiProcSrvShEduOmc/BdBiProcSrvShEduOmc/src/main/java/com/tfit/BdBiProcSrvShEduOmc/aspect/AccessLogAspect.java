package com.tfit.BdBiProcSrvShEduOmc.aspect;

import com.tfit.BdBiProcSrvShEduOmc.annotation.AccessLog;
import com.tfit.BdBiProcSrvShEduOmc.util.HttpContextUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.IPUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Descritpion：Api访问日志记录切面
 * @author: tianfang_infotech
 * @date: 2019/1/3 17:03
 */
@Aspect
@Configuration
public class AccessLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogAspect.class);

    @Around("execution(* com.tfit.BdBiProcSrvShEduOmc.controller..*(..)) && @annotation(com.tfit.BdBiProcSrvShEduOmc.annotation.AccessLog)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {

        long beginTime = System.currentTimeMillis();

        Object result = pjp.proceed();

        try {

            MethodSignature signature = (MethodSignature) pjp.getSignature();
            Method method = signature.getMethod();

            AccessLog lock = method.getAnnotation(AccessLog.class);

            if (Objects.isNull(lock)) {
                //不包含指定注解，不记录
                return result;
            }

            long executedTime = System.currentTimeMillis() - beginTime;

            // 请求的方法名
            String className = pjp.getTarget().getClass().getName();
            String methodName = signature.getName();

            // 请求的方法参数值
            Object[] args = pjp.getArgs();

            // 请求的方法参数名称
            LocalVariableTableParameterNameDiscoverer parameters = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = parameters.getParameterNames(method);
            String parameter = "";
            if (args != null && paramNames != null) {
                String params = "";
                for (int i = 0; i < args.length; i++) {
                    params += "  " + paramNames[i] + ": " + args[i];
                }

                parameter = params;
            }

            //获取request
            HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
            //设置IP地址
            String ipAddress = IPUtil.getIpAddress(request);

            logger.info("[api-accessLog] ==> {}.{}() --> parameters:{}, executedTime:{} ms, ipAddress:{}, result:{}", className, methodName, parameter, executedTime, ipAddress, result);

        } catch (Exception ex) {
            ex.printStackTrace();
            return result;
        }

        return result;
    }
}
