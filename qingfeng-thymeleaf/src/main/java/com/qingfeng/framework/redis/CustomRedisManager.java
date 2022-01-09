package com.qingfeng.framework.redis;

import org.crazycake.shiro.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 * @ProjectName CustomRedisManager
 * @author Administrator
 * @version 1.0.0
 * @Description
 * 自定义org.crazycake.shiro.RedisManager。<br/>
 * 该自定义的Manager扩展的功能：<br/>
 * 1.增加database参数，可跟随配置文件的数据库索引选择redis数据库保存相关数据，避免了多个项目共用一个redis database所潜在的问题<br/>
 * 2.修改expire参数，默认值为30天 = 2592000s <br/>
 * @createTime 2021/12/30 0030 21:02
 */
public class CustomRedisManager extends RedisManager {

    @Autowired
    private RedisProperties redisProperties;

    private static JedisPool jedisPool = null;
    private String host = "127.0.0.1";
    private int port = 6379;
    private int expire = 2592000;
    private int timeout = 5000;
    private int database = 0;
    private String password = "";

//    public CustomRedisManager() {
//        this.password = StringUtils.isEmpty(this.password) ? null : this.password;
//        if (jedisPool == null) {
//            jedisPool = new JedisPool(new JedisPoolConfig(), this.host, this.port, this.timeout, this.password, database);
//        }
//    }

    public void init(){
        this.password = StringUtils.isEmpty(this.password) ? null : this.password;
        if (jedisPool == null) {
            jedisPool = new JedisPool(new JedisPoolConfig(), this.host, this.port, this.timeout, this.password, database);
        }
    }


    @Override
    public byte[] get(byte[] key) {
        byte[] value = null;

        try (Jedis jedis = jedisPool.getResource()) {
            value = jedis.get(key);
        }

        return value;
    }

    public byte[] set(byte[] key, byte[] value) {

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
            if (this.expire != 0) {
                jedis.expire(key, this.expire);
            }
        }

        return value;
    }

    @Override
    public byte[] set(byte[] key, byte[] value, int expire) {

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
            if (expire != 0) {
                jedis.expire(key, expire);
            }
        }

        return value;
    }

    @Override
    public void del(byte[] key) {

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }

    }

    public void flushDB() {

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }

    }

    public Long dbSize() {
        Long dbSize = 0L;

        try (Jedis jedis = jedisPool.getResource()) {
            dbSize = jedis.dbSize();
        }

        return dbSize;
    }

    public Set<byte[]> keys(String pattern) {
        Set<byte[]> keys = null;

        try (Jedis jedis = jedisPool.getResource()) {
            keys = jedis.keys(pattern.getBytes());
        }

        return keys;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getExpire() {
        return this.expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    @Override
    public int getTimeout() {
        return this.timeout;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        if (null == database) {
            return;
        }
        this.database = database;
    }
}
