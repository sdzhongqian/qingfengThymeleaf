package com.qingfeng.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * @title 配置信息
 * @description 配置信息
 * @author Administrator
 * @updateTime 2021/7/22 0022 0:33
 */
@Configuration
public class I18nConfig extends WebMvcConfigurationSupport {
    /**
     * session区域解析器
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.CHINA);

        return resolver;
    }


    /**
     * cookie区域解析器
     * @return
     */
//    @Bean
//    public LocaleResolver localeResolver() {
//        CookieLocaleResolver slr = new CookieLocaleResolver();
//        //设置默认区域,
//        slr.setDefaultLocale(Locale.CHINA);
//        slr.setCookieMaxAge(3600);//设置cookie有效期.
//        return slr;
//    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        // 设置请求地址的参数,默认为：locale
//        lci.setParamName(LocaleChangeInterceptor.DEFAULT_PARAM_NAME);
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

}
