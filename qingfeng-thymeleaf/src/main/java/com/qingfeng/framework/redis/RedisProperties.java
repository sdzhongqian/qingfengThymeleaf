package com.qingfeng.framework.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @ProjectName RedisProperties
 * @author Administrator
 * @version 1.0.0
 * @Description redis属性配置文件
 * @createTime 2021/12/30 0030 21:02
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
@EqualsAndHashCode(callSuper = false)
@Order(-1)
public class RedisProperties {
    private Integer database;
    private String host;
    private Integer port;
    private String password;
    private Integer timeout;
    /**
     * 默认30天 = 2592000s
     */
    private Integer expireDate30 = 30*24*60 * 60;

    /**
     * 默认30分钟 = 1800000
     */
    private Integer expireMinute30 = 30 * 60 * 60;

}
