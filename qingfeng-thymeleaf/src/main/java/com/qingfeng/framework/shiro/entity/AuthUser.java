package com.qingfeng.framework.shiro.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @ProjectName AuthUser
 * @author Administrator
 * @version 1.0.0
 * @Description 权限用户
 * @createTime 2021/12/30 0030 21:05
 */
@Data
public class AuthUser implements Serializable {

    public String id;
    public String login_name;
    public String name;

}
