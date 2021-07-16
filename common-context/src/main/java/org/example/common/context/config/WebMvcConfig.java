package org.example.common.context.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.example.common.context.interceptor.RequestContextInterceptor;

/**
 * @author 李红兵
 * @date 2021/4/23 15:34
 * @description WebMvcConfig
 */
@Configuration
@Component("RequestContextWebMvcConfig")
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestContextInterceptor());
    }
}
