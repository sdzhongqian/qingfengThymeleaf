package com.qingfeng.framework.shiro.realm;

import com.qingfeng.system.service.LoginService;
import com.qingfeng.system.service.MenuService;
import com.qingfeng.system.service.RoleService;
import com.qingfeng.system.service.UserService;
import com.qingfeng.util.PageData;
import com.qingfeng.util.Verify;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ProjectName ShiroRealm
 * @author Administrator
 * @version 1.0.0
 * @Description  Shiro-密码输入错误的状态下重试次数的匹配管理
 * @createTime 2021/12/30 0030 21:05
 */
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;
    @Autowired
    public LoginService loginService;
    @Autowired
    private RoleService roleService;

    /**
     * 提供账户信息返回认证信息（用户的角色信息集合）
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        PageData pd = new PageData();
        //获取用户的输入的账号.
        String username = (String) token.getPrincipal();
        pd.put("login_name",username);
        PageData userPd = loginService.findUserInfo(pd);
        if(Verify.verifyIsNull(userPd)){
            throw new UnknownAccountException("账号不存在！");
        }
        if (!userPd.get("status").equals("0")) {
            throw new LockedAccountException("帐号已休眠或已被锁定，禁止登录！");
        }
//        System.out.println("###########################################");
        // 当验证都通过后，把用户信息放在session里
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("loginUser", userPd);
        //查询当前用户组织
        pd.put("user_id",userPd.get("id"));
        PageData orgPd = userService.findUserOrganizeInfo(pd);
        session.setAttribute("loginOrganize", orgPd);

        // principal参数使用用户Id，方便动态刷新用户权限
        return new SimpleAuthenticationInfo(
                userPd.get("id"),
                userPd.get("login_password"),
                ByteSource.Util.bytes(username),
                getName()
        );
    }

    /**
     * 权限认证，为当前登录的Subject授予角色和权限（角色的权限信息集合）
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        String userId = SecurityUtils.getSubject().getPrincipal().toString();

        // 赋予角色
        PageData pd = new PageData();
        pd.put("user_id",userId);
        List<PageData> roleList = roleService.findUserRoleList(pd);
        for (PageData rolePd : roleList) {
            info.addRole(rolePd.get("name").toString());
        }

        // 赋予权限
        List<PageData> menuList = null;
        pd.put("id",userId);
        PageData userPd = userService.findInfo(pd);
        if(Verify.verifyIsNull(userPd)){
            return info;
        }
        // ROOT用户默认拥有所有权限
        if (userPd.get("status").equals("0")) {
            menuList = menuService.findAuthMenuList(pd);
        } else {
            pd.put("user_id",userPd.get("id"));
            menuList = menuService.findAuthMenuList(pd);
        }

        if (!CollectionUtils.isEmpty(menuList)) {
            Set<String> permissionSet = new HashSet<String>();
            for (PageData mPd : menuList) {
                if (!StringUtils.isEmpty(mPd.get("code"))) {
                    String permission = mPd.get("parent_code")+":"+mPd.get("code").toString();
                    permissionSet.addAll(Arrays.asList(permission.trim().split(",")));
                    //列表页面index和findListPage 如：user=> userList
                    permissionSet.add(mPd.get("parent_code").toString()+"List");
                }
            }
            info.setStringPermissions(permissionSet);
        }
        return info;
    }

}
