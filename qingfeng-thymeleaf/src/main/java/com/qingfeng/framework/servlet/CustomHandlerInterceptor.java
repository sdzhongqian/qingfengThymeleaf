package com.qingfeng.framework.servlet;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingfeng.system.service.MenuService;
import com.qingfeng.util.Const;
import com.qingfeng.util.Json;
import com.qingfeng.util.PageData;
import com.qingfeng.util.Verify;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.List;

/**
 * @Title: CustomHandlerInterceptor
 * @ProjectName wdata
 * @Description: 登录拦截器
 * @author anxingtao
 * @date 2020-9-27 11:27
 */
public class CustomHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private MenuService menuService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getServletPath();
//        System.out.println("path:"+path);
//        System.out.println("getContextPath:"+request.getContextPath());
//        System.out.println("getAuthType:"+request.getAuthType());
//        System.out.println("getMethod:"+request.getMethod());
//        System.out.println("getRequestURI:"+request.getRequestURI());
//        System.out.println("getLocalName:"+request.getLocalName());
        PageData pd = new PageData(request);
//        System.out.println(pd);

        boolean bol = false;
        if(path.matches(Const.NO_INTERCEPTOR_PATH)){
            bol = true;
        }else {
            HttpSession session = request.getSession();
            PageData user = (PageData) session.getAttribute("loginUser");
            PageData organize = (PageData) session.getAttribute("loginOrganize");
            if (user != null && organize!=null) {
                bol = true;
                //根据path查询子菜单
                if(Verify.verifyIsNotNull(pd.get("menuAuthId"))){//查询功能按钮权限
                    pd.put("user_id",user.get("id"));
                    pd.put("organize_id",organize.get("organize_id"));
                    pd.put("type","button");
                    pd.put("parent_id",pd.get("menuAuthId"));
                    pd.put("url_path",path);
                    if (menuService == null) {//解决service为null无法注入问题
                        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
                        menuService = (MenuService) factory.getBean("menuService");
                    }
                    List<PageData> buttonLs = menuService.findMenuList(pd);
                    pd.put("id",pd.get("menuAuthId"));
                    PageData mPd = menuService.findInfo(pd);
                    StringBuffer sb = new StringBuffer();
                    for (int bi = 0; bi < buttonLs.size(); bi++) {
                        sb.append(mPd.get("code")+":"+buttonLs.get(bi).get("code"));
                        sb.append(";");
                    }
                    request.setAttribute("menuAuthParams",sb);
                }
            } else {
                //如果是ajax请求响应头会有x-requested-with、
                if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
                    ServletOutputStream out = response.getOutputStream();
                    Json json = new Json();
                    json.setSuccess(false);
                    json.setUrl(request.getContextPath() + "/system/login/login");
                    json.setLoseSession("loseSession");
                    json.setMsg("登录失效，正在跳转。。。");
                    response.setContentType("text/html;charset=utf-8");
                    ObjectMapper objMapper = new ObjectMapper();
                    JsonGenerator jsonGenerator = objMapper.getJsonFactory()
                            .createJsonGenerator(response.getOutputStream(),
                                    JsonEncoding.UTF8);

                    jsonGenerator.writeObject(json);
                    jsonGenerator.flush();
                    jsonGenerator.close();

                    bol = false;
                } else {
                    //登陆过滤
                    PrintWriter out = response.getWriter();
                    out.println("<html>");
                    out.println("<script>");
                    out.println("window.open ('"+request.getContextPath()+"/system/login/login','_top')");
                    out.println("</script>");
                    out.println("</html>");
                    bol = false;
                }
            }

        }
        return bol;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }
}