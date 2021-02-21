package com.tfit.BdBiProcSrvShEduOmc.interceptor;

import com.tfit.BdBiProcSrvShEduOmc.annotation.CheckUserToken;
import com.tfit.BdBiProcSrvShEduOmc.config.UserAuthConfig;
import com.tfit.BdBiProcSrvShEduOmc.context.UserContextHandler;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.exception.BusinessException;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.BasicBdUser;
import com.tfit.BdBiProcSrvShEduOmc.service.edubd.EduBdUserService;
import com.tfit.BdBiProcSrvShEduOmc.util.DictConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Descritpion：用户身份验证拦截器
 * @author: tianfang_infotech
 * @date: 2019/1/21 11:59
 */
public class UserAuthInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthInterceptor.class);

    /**
     * token 长度
     */
    private static final int TOKEN_LENGTH = 40;

    @Autowired
    private UserAuthConfig userAuthConfig;

    @Autowired
    EduBdUserService eduBdUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        logger.info("[UserAuthInterceptor] => intoHandle");

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //配置该注解，进行用户拦截(先检查类注解，后检查方法注解)
        CheckUserToken annotation = handlerMethod.getBeanType().getAnnotation(CheckUserToken.class);
        if (annotation == null) {
            annotation = handlerMethod.getMethodAnnotation(CheckUserToken.class);
        }

        //如没发现注解，取消拦截
        if (annotation == null) {
            return super.preHandle(request, response, handler);
        }

        String token = request.getHeader(userAuthConfig.getHeaderToken());

        if (StringUtils.isEmpty(token)) {

            logger.info("[UserAuthInterceptor] => validate token failure, tokenValue is empty");

            //身份验证失败
            throw new BusinessException(IOTRspType.AUTHCODE_CHKERR);
        }

        //validate token
      BasicBdUser basicBdUser = eduBdUserService.getBasicBdUser(token);
        if (basicBdUser == null || StringUtils.isEmpty(basicBdUser.getToken()) || !basicBdUser.getToken().equalsIgnoreCase(token)
                || basicBdUser.getToken().length() != TOKEN_LENGTH) {

            logger.info("[UserAuthInterceptor] => validate token failure, tokenValue: " + token);

            //身份验证失败
            throw new BusinessException(IOTRspType.AUTHCODE_CHKERR);
        }

        logger.info("[UserAuthInterceptor] => tokenValue: " + token);

        String distCode = DictConvertUtil.mapToOrgCode(basicBdUser.getOrgName());

        logger.debug("[UserAuthInterceptor] => token.distCode:{}", distCode);

        UserContextHandler.setToken(token);
        UserContextHandler.setDistrictCode(distCode);

        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //整个请求处理完毕，清除资源
        UserContextHandler.remove();

        super.afterCompletion(request, response, handler, ex);
    }
}
