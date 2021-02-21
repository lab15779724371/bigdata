package com.tfit.BdBiProcSrvShEduOmc.config;

import com.tfit.BdBiProcSrvShEduOmc.interceptor.UserAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Descritpion：Web身份认证配置
 * @author: tianfang_infotech
 * @date: 2019/1/21 11:54
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(getUserAuthInterceptor()).addPathPatterns("/**").excludePathPatterns(getExcludeCommonPathPatterns());

        super.addInterceptors(registry);
    }

    /**
     * 注册 UserAuthConfig Bean
     * @return
     */
    @Bean
    UserAuthConfig getUserAuthConfig() {
        return new UserAuthConfig();
    }

    @Bean
    UserAuthInterceptor getUserAuthInterceptor() {
        return new UserAuthInterceptor();
    }

    private String[] getExcludeCommonPathPatterns() {
        String[] urls = {
                "/swagger-resources/**",
                "/cache/**",
        };

        return urls;
    }
}
