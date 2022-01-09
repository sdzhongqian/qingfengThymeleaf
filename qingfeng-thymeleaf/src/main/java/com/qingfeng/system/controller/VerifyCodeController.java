package com.qingfeng.system.controller;

import com.qingfeng.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName VerifyCodeController
 * @author qingfeng
 * @version 1.0.0
 * @Description verifyCode
 * @createTime 2021/12/28 0028 16:05
 */
@Controller
@RequestMapping("/system/verifyCode")
public class VerifyCodeController {

    @Autowired
    private RedisTemplate redisTemplate;
    // 设置会话的过期时间
    private int seconds = 5;

    /**
     * @title getSysCode
     * @description 获取验证码
     * @author qingfeng
     * @updateTime 2021/12/28 0028 16:22
     */
    @RequestMapping(value = "/getSysCode", method = RequestMethod.GET)
    public void getSysCode(HttpServletRequest request,HttpServletResponse response){
        PageData pd = new PageData(request);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String code = drawImg(output);
        redisTemplate.opsForValue().set("sysVerifyCode_"+pd.get("key"),code,seconds, TimeUnit.MINUTES);
        try {
            ServletOutputStream out = response.getOutputStream();
            output.writeTo(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String drawImg(ByteArrayOutputStream output){
        String code = "";
        for(int i=0; i<5; i++){
            code += randomChar();
        }
        int width = 120;
        int height = 45;
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
        Font font = new Font("Times New Roman",Font.PLAIN,32);
        Graphics2D g = bi.createGraphics();
        g.setFont(font);
        Color color = new Color(66,2,82);
        g.setColor(color);
        g.setBackground(new Color(226,226,240));
        g.clearRect(0, 0, width, height);
        FontRenderContext context = g.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(code, context);
        double x = (width - bounds.getWidth()) / 2;
        double y = (height - bounds.getHeight()) / 2;
        double ascent = bounds.getY();
        double baseY = y - ascent;
        g.drawString(code, (int)x, (int)baseY);

        int red = 0, green = 0, blue = 0;
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            // 设置随机开始和结束坐标
            int xs = random.nextInt(width);// x坐标开始
            int ys = random.nextInt(height);// y坐标开始
            int xe = xs + random.nextInt(width);// x坐标结束
            int ye = ys + random.nextInt(height);// y坐标结束

            // 产生随机的颜色值，让输出的每个干扰线的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawLine(xs, ys, xe, ye);
        }
        g.dispose();
        try {
            ImageIO.write(bi, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    private char randomChar(){
        Random r = new Random();
        String s = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789";
        return s.charAt(r.nextInt(s.length()));
    }
}
