package com.qingfeng.system.controller;

import com.qingfeng.base.controller.BaseController;
import com.qingfeng.framework.monitor.server.MySessionContext;
import com.qingfeng.framework.monitor.server.UserOnlineListener;
import com.qingfeng.system.service.LoggerService;
import com.qingfeng.system.service.LoginService;
import com.qingfeng.system.service.UserService;
import com.qingfeng.util.*;
import com.qingfeng.util.upload.ParaUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by anxingtao on 2020-9-27.
 */
@Controller
@RequestMapping(value = "/system/login")
public class LoginController extends BaseController {

    @Autowired
    public LoginService loginService;
    @Autowired
    private UserService userService;
    @Autowired
    private LoggerService loggerService;

    /**
     * @Description: login
     * @Param: [request, response, session]
     * @return: java.lang.String
     * @Author: anxingtao
     * @Date: 2020-9-27 9:09
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        PageData pd = new PageData(request);
        session.removeAttribute("loginUser");//清除用户
        session.removeAttribute("loginOrganize");//清除组织
        session.removeAttribute("userOnlineListener");//移除在线用户
        Enumeration em = session.getAttributeNames();
        while(em.hasMoreElements()){
            session.removeAttribute(em.nextElement().toString());
        }
        MySessionContext mySessionContext=MySessionContext.getInstance();
        mySessionContext.delSession(session);
        return "web/system/login/login";
    }

    /** 
     * @Description: postLogin 
     * @Param: [request, response, session] 
     * @return: void 
     * @Author: anxingtao
     * @Date: 2020-9-27 10:42 
     */ 
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void postLogin(HttpServletRequest request, HttpServletResponse response,HttpSession session) throws Exception {
        PageData pd = new PageData(request);
        String errMsg = "";
        String errCode = "0";
        boolean errBol = false;
        if(!Verify.verifyIsNotNull(pd.getString("login_name"))){
            errMsg="登录名称不可为空。";
            errCode = "1";
        }else if(!Verify.verifyIsNotNull(pd.getString("password"))){
            errMsg="登录密码不可为空。";
            errCode = "2";
        }else{
            PageData uPd = loginService.findUserInfo(pd);
            if(Verify.verifyIsNotNull(uPd)){
                if(uPd.getString("status").equals("2")){
                    errCode = "1";
                    errMsg="账号已休眠，请联系管理员";
                }else{
                    if(uPd.getString("login_password").equals(PasswordUtil.encrypt(pd.get("password").toString(), uPd.get("login_name").toString()))){
                        if(uPd.getString("status").equals("0")){
                            //登录成功
                            errBol=true;
                            if(Verify.verifyIsNotNull(uPd.get("head_address"))){
                                uPd.put("head_address", ParaUtil.cloudfile+uPd.get("head_address"));
                            }
                            uPd.put("login_success_time", DateTimeUtil.getDateTimeStr());
                            session.setAttribute("loginUser", uPd);
                            //查询当前用户组织
                            pd.put("user_id",uPd.get("id"));
                            PageData orgPd = userService.findUserOrganizeInfo(pd);
                            session.setAttribute("loginOrganize", orgPd);
                            session.setMaxInactiveInterval(30*60);
                            //添加在线用户
                            PageData uParam = new PageData();
                            uParam.put(uPd.get("id").toString(),session.getId());
                            session.setAttribute("userOnlineListener", new UserOnlineListener(uParam));
                            MySessionContext mySessionContext=MySessionContext.getInstance();
                            mySessionContext.addSession(session);

                            PageData p = new PageData();
                            //主键id
                            p.put("id", GuidUtil.getUuid());
                            p.put("type","1");
                            p.put("title",uPd.get("name")+"在"+DateTimeUtil.getDateTimeStr()+"进行了登录操作");
                            p.put("content",uPd.get("name")+"在"+DateTimeUtil.getDateTimeStr()+"进行了登录操作");
                            p.put("operate_type","POST");
                            p.put("operate_user",uPd.get("name"));
                            p.put("create_user",uPd.get("id"));
                            p.put("create_time", DateTimeUtil.getDateTimeStr());
                            p.put("update_time",DateTimeUtil.getDateTimeStr());
                            loggerService.save(p);

                            int pwd_error_num = 0;
                            String time = DateTimeUtil.getDateTimeStr();
                            PageData pu = new PageData();
                            pu.put("id",uPd.get("id"));
                            pu.put("pwd_error_num",pwd_error_num);
                            pu.put("pwd_error_time",time);
                            pu.put("update_time",time);
                            pu.put("last_login_time",time);
                            pu.put("update_user",uPd.get("id"));
                            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
                            // 获取客户端操作系统
                            String os = userAgent.getOperatingSystem().getName();
                            // 获取客户端浏览器
                            String browser = userAgent.getBrowser().getName();
                            pu.put("browser",browser);
                            pu.put("os",os);
                            pu.put("ipaddr",IpUtils.getIpAddr(request));
                            pu.put("iprealaddr",AddressUtils.getRealAddressByIP(IpUtils.getIpAddr(request)));
                            int num = userService.update(pu);
                        }else if(uPd.getString("status").equals("N")){
                            errCode = "1";
                            errMsg="账号已禁用，请联系管理员";
                        }else if(uPd.getString("status").equals("2")){
                            errCode = "1";
                            errMsg="账号已休眠，请联系管理员";
                        }
                    }else{
                        //记录密码次数。
                        int pwd_error_num = 0;
                        if(Verify.verifyIsNotNull(uPd.get("pwd_error_num"))){
                            pwd_error_num = Integer.parseInt(uPd.get("pwd_error_num").toString())+1;
                        }else{
                            pwd_error_num = 1;
                        }
                        String time = DateTimeUtil.getDateTimeStr();
                        PageData p = new PageData();
                        p.put("id",uPd.get("id"));
                        p.put("pwd_error_num",pwd_error_num);
                        p.put("pwd_error_time",time);
                        p.put("update_time",time);
                        p.put("update_user",uPd.get("id"));
                        if(pwd_error_num>5){
                            p.put("status","2");
                        }
                        userService.update(p);
                        errCode = "2";
                        if((6-pwd_error_num)>0){
                            errMsg="您所填写的密码不正确，还有"+(6-pwd_error_num)+"次机会！";
                        }else{
                            errMsg="账号已休眠，请联系管理员。";
                        }
                    }
                }
            }else{
                errCode = "1";
                errMsg="登录名称不存在，请重新输入。";
            }
        }
        Json json = new Json();
        json.setMsg(errMsg);
        json.setFlag(errCode);
        json.setSuccess(errBol);
        this.writeJson(response,json);
    }



}
