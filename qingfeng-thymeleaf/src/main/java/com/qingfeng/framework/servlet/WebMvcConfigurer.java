package com.qingfeng.framework.servlet;

import com.qingfeng.util.upload.ParaUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @ProjectName WebMvcConfigurer
 * @author Administrator
 * @version 1.0.0
 * @Description 拦截器
 * @createTime 2021/12/30 0030 21:35
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurationSupport {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("file:" + ParaUtil.localName);
        super.addResourceHandlers(registry);
    }


}
