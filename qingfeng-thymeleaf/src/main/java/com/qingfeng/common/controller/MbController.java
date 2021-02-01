package com.qingfeng.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by anxingtao on 2020-10-18.
 */
@Controller
@RequestMapping("/mb")
public class MbController {

    @GetMapping(value = "/test")
    public ModelAndView test(HttpServletRequest req) {
        // UserEntity userEntity = getCurrentUser(req);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("hello/hello");
        return mv;
    }

}
