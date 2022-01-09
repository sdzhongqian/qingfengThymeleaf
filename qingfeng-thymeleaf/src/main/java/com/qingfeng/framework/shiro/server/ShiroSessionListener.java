package com.qingfeng.framework.shiro.server;

import com.qingfeng.util.PageData;
import com.qingfeng.util.Verify;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @ProjectName ShiroSessionListener
 * @author Administrator
 * @version 1.0.0
 * @Description ShiroSessionListener
 * @createTime 2021/12/30 0030 21:06
 */
public class ShiroSessionListener  implements SessionListener{
    private static final Logger logger = LoggerFactory.getLogger(ShiroSessionListener.class);

    @Autowired
    private RedisSessionDao sessionDao;
    //在线用户前缀
    private String prefix = "onlineUser:";
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onStart(Session session) {
        // 会话创建时触发
        logger.info("ShiroSessionListener session {} 被创建", session.getId());
    }

    @Override
    public void onStop(Session session) {
        sessionDao.delete(session);
        // 会话被停止时触发
        logger.info("ShiroSessionListener session {} 被销毁", session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        if(Verify.verifyIsNotNull(session.getAttribute("loginUser"))){
            PageData uPd = (PageData) session.getAttribute("loginUser");
            System.out.println(prefix+uPd.get("id"));
            redisTemplate.delete(prefix+uPd.get("id"));//清除在线用户
        }
        sessionDao.delete(session);
        //会话过期时触发
        logger.info("ShiroSessionListener session {} 过期", session.getId());
    }
}
