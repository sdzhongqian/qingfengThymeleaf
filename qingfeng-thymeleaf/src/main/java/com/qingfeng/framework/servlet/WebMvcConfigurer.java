package com.qingfeng.framework.servlet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 拦截器
 * Created by anxingtao on 2018-8-19.
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurationSupport {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

//注册拦截器 拦截规则
//多个拦截器时 以此添加 执行顺序按添加顺序
//        registry.addInterceptor(getHandlerInterceptor()).addPathPatterns("/**/*.do*").addPathPatterns("/");
        registry.addInterceptor(getHandlerInterceptor()).addPathPatterns("/")
                .addPathPatterns("/system/**")
                .addPathPatterns("/common/**")
                .addPathPatterns("/quartz/**")
                .addPathPatterns("/monitor/**")
                .addPathPatterns("/gencode/**")
                .addPathPatterns("/customize/**");
    }

    //后台
    @Bean
    public static HandlerInterceptor getHandlerInterceptor() {
        return new CustomHandlerInterceptor();
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("forward:/index");
//        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        super.addViewControllers(registry);
//    }


}
