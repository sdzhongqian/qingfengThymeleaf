package com.qingfeng.example.controller;

import com.qingfeng.base.controller.BaseController;
import com.qingfeng.util.Json;
import com.qingfeng.util.MessageUtil;
import com.qingfeng.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * @ProjectName LanguageController
 * @author Administrator
 * @version 1.0.0
 * @Description 设置语言环境
 * @createTime 2021/7/24 0024 19:31
 */
@Controller
@RequestMapping(value = "/example/language")
public class LanguageController extends BaseController {


    @Autowired
    private MessageUtil messageUtil;

    /**
     * @title index
     * @description 首页
     * @author Administrator
     * @updateTime 2021/7/24 0024 20:40
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException {
        PageData pd = new PageData(request);
        map.put("pd",pd);
        return "web/example/language/index";
    }


    /**
     * 设置语言
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/setLang")
    public void getInfoByLang(HttpServletRequest request, HttpServletResponse response) throws Exception{
        PageData pd = new PageData(request);
        String lang = pd.get("lang").toString();
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if("zh".equals(lang)){
            localeResolver.setLocale(request, response, new Locale("zh", "CN"));
        }else if("en".equals(lang)){
            localeResolver.setLocale(request, response, new Locale("en", "US"));
        }
        Json json = new Json();
        json.setMsg("设置"+lang+"成功");
        json.setSuccess(true);
        this.writeJson(response,json);
    }


    /**
     * 根据key  获取内容
     * @param key
     * @return
     */
    @GetMapping("/getValue")
    public void getValue(String key,HttpServletResponse response) throws Exception {
        String welcome = messageUtil.getMessage(key);
        Json json = new Json();
        json.setMsg(welcome);
        json.setSuccess(true);
        this.writeJson(response,json);
    }

}
