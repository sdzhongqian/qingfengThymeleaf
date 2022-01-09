package com.qingfeng.framework.shiro.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.qingfeng.framework.redis.CustomRedisManager;
import com.qingfeng.framework.redis.RedisProperties;
import com.qingfeng.framework.shiro.credentials.RetryLimitCredentialsMatcher;
import com.qingfeng.framework.shiro.filter.KickoutSessionFilter;
import com.qingfeng.framework.shiro.filter.OnlineSessionFilter;
import com.qingfeng.framework.shiro.filter.SyncOnlineSessionFilter;
import com.qingfeng.framework.shiro.realm.ShiroRealm;
import com.qingfeng.framework.shiro.server.RedisSessionDao;
import com.qingfeng.framework.shiro.server.ShiroSessionListener;
import com.qingfeng.framework.shiro.server.ShiroSessonFactory;
import com.qingfeng.framework.shiro.service.ShiroService;
import com.qingfeng.framework.shiro.session.ShiroSessionManager;
import com.qingfeng.util.spring.SpringUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName ShiroConfig
 * @author Administrator
 * @version 1.0.0
 * @Description Shiro配置类
 * @createTime 2021/12/30 0030 21:04
 */
@Configuration
@Order(-1)
public class ShiroConfig {

    @Autowired
    private ShiroService shiroService;
    @Autowired
    private RedisProperties redisProperties;
    @Value("${csrf.domains}")
    private String csrf_domains;

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean(SecurityManager securityManager){
        MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
        bean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        bean.setArguments(securityManager);
        return bean;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，因为在
     * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
     * Filter Chain定义说明
     * 1、一个URL可以配置多个Filter，使用逗号分隔
     * 2、当设置多个过滤器时，全部验证通过，才视为通过
     * 3、部分过滤器可指定参数，如perms，roles
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/system/login/login");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/main");
        // 未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/noAuth");
        // 配置数据库中的resource
        Map<String, String> filterChainDefinitionMap = shiroService.loadFilterChainDefinitions();

        Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
        filters.put("onlineSession", onlineSessionFilter());
        filters.put("syncOnlineSession", syncOnlineSessionFilter());
        //限制同一帐号同时在线的个数
        filters.put("kickout", kickoutSessionFilter());
        //加入csrfFilter拦截器
//        filters.put("csrfFilter", csrfFilter());
        shiroFilterFactoryBean.setFilters(filters);

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 自定义在线用户处理过滤器
     */
    @Bean
    public OnlineSessionFilter onlineSessionFilter()
    {
        OnlineSessionFilter onlineSessionFilter = new OnlineSessionFilter();
        return onlineSessionFilter;
    }


    /**
     * 自定义在线用户同步过滤器
     */
    @Bean
    public SyncOnlineSessionFilter syncOnlineSessionFilter()
    {
        SyncOnlineSessionFilter syncOnlineSessionFilter = new SyncOnlineSessionFilter();
        return syncOnlineSessionFilter;
    }



    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Bean(name = "securityManager")
    public SecurityManager securityManager(@Qualifier("shiroRealm") ShiroRealm authRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置realm.
        securityManager.setRealm(authRealm);
        securityManager.setCacheManager(redisCacheManager());
        // 自定义session管理 使用redis
        securityManager.setSessionManager(sessionManager());
        // 注入记住我管理器
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    @Bean(name = "shiroRealm")
    public ShiroRealm shiroRealm(@Qualifier("credentialsMatcher") RetryLimitCredentialsMatcher matcher) {
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCredentialsMatcher(credentialsMatcher());
        return shiroRealm;
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     * 所以我们需要修改下doGetAuthenticationInfo中的代码;
     * ）
     *
     * @return
     */
    @Bean(name = "credentialsMatcher")
    public RetryLimitCredentialsMatcher credentialsMatcher() {
        return new RetryLimitCredentialsMatcher();
    }


    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 配置shiro redisManager
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    public RedisManager redisManager() {
        CustomRedisManager redisManager = new CustomRedisManager();
        redisManager.setHost(redisProperties.getHost());
        redisManager.setPort(redisProperties.getPort());
        redisManager.setDatabase(redisProperties.getDatabase());
        redisManager.setTimeout(redisProperties.getTimeout());
        redisManager.setPassword(redisProperties.getPassword());
        redisManager.init();
        return redisManager;
    }

    /**
     * cacheManager 缓存 redis实现
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }


    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * 使用的是shiro-redis开源插件
     */
//    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    /**
     * shiro session的管理
     */
//    @Bean
//    public DefaultWebSessionManager sessionManager() {
//        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
//        sessionManager.setGlobalSessionTimeout(redisProperties.getExpireMinute30() * 1000L);
//        sessionManager.setSessionDAO(redisSessionDAO());
//        return sessionManager;
//    }

    /**
     * cookie对象;
     *
     * @return
     */
    public SimpleCookie rememberMeCookie() {
        // 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setName("QingFeng");
        // 记住我cookie生效时间30天 ,单位秒。 注释掉，默认永久不过期 2018-07-15
        simpleCookie.setMaxAge(redisProperties.getExpireDate30() * 1000);
        return simpleCookie;
    }

    /**
     * cookie管理对象;记住我功能
     *
     * @return
     */
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
//        cookieRememberMeManager.setCipherKey(Base64.decode("1QWLxg+NYmxraMoxAXu/Iw=="));
        cookieRememberMeManager.setCipherKey(Base64.decode("NcKMteWtmFtP5xxJWPOujg=="));
        return cookieRememberMeManager;
    }


    //=====================shiro session =================================
    @Bean
    public SessionManager sessionManager(){
//    public SessionManager sessionManager(@Autowired SessionListener sessionListener, @Autowired RedisSessionDao redisSessionDao){
        ShiroSessionManager sessionManager = new ShiroSessionManager();
        sessionManager.setGlobalSessionTimeout(redisProperties.getExpireMinute30() * 1000);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationInterval(redisProperties.getExpireMinute30() * 1000);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionFactory(new ShiroSessonFactory());
        sessionManager.setSessionDAO(SpringUtils.getBean(RedisSessionDao.class));
        List<SessionListener> sessionListeners = new ArrayList<SessionListener>();
        sessionListeners.add(SpringUtils.getBean(SessionListener.class));
        sessionManager.setSessionListeners(sessionListeners);
        sessionManager.setSessionIdCookie(cookieDAO());
        return sessionManager;
    }

    //同一个域下两个项目使用shiro，cookie值相同相互影响
    @Bean
    public Cookie cookieDAO() {
       Cookie cookie=new org.apache.shiro.web.servlet.SimpleCookie();
       cookie.setName("qingfeng");
       return cookie;
    }

    @Bean("sessionLisener")
    public SessionListener sessionListener(){
        return new ShiroSessionListener();
    }


    /**
     * 并发登录控制
     * @return
     */
    @Bean
    public KickoutSessionFilter kickoutSessionFilter(){
        KickoutSessionFilter kickoutSessionFilter = new KickoutSessionFilter();
        //用于根据会话ID，获取会话进行踢出操作的；
        kickoutSessionFilter.setSessionManager(sessionManager());
        //使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
        kickoutSessionFilter.setCacheManager(redisCacheManager());
        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；
        kickoutSessionFilter.setKickoutAfter(false);
        //同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
        kickoutSessionFilter.setMaxSession(1);
        //被踢出后重定向到的地址；
        kickoutSessionFilter.setKickoutUrl("/system/login/login?kickout=1");
        return kickoutSessionFilter;
    }


//    @Bean
//    public CsrfFilter csrfFilter() {
//        // 这里使用的是配置文件信息，获取配置文件中的csrf.domains相关值信息
//        String csrfDomains = csrf_domains;
//        List list = Collections.<String>emptyList();
//        if (StringUtils.isNotBlank(csrfDomains)) {
//            list = Arrays.asList(csrfDomains.split(","));
//        }
//        CsrfFilter csrfFilter = new CsrfFilter(list);
//        return csrfFilter;
//    }


//    @Bean
//    public FilterRegistrationBean csrfFilter() {
//        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
//        // 这里使用的是配置文件信息，获取配置文件中的csrf.domains相关值信息
//        String csrfDomains = properties().getProperty("csrf.domains");
//        if (StringUtils.isNotBlank(csrfDomains)) {
//            filterRegistration.setFilter(new CsrfFilter(Arrays.asList(csrfDomains.split(","))));
//        } else {
//            filterRegistration.setFilter(new CsrfFilter(Collections.<String>emptyList()));
//        }
//        filterRegistration.setEnabled(true);
//        filterRegistration.addUrlPatterns("/*");
//        return filterRegistration;
//    }

}
