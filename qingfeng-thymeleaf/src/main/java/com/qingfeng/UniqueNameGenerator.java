package com.qingfeng;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfengThymeleaf
 * @Description sping下代码混淆后，不同包下的bean名相同会报bean冲突
 * @createTime 2022年04月25日 22:59:00
 */
public class UniqueNameGenerator extends AnnotationBeanNameGenerator {
    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        //全限定类名
        String beanName = definition.getBeanClassName();
        return beanName;
    }
}