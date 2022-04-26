package com.qingfeng.framework.servlet;

/**
 * @author anxingtao
 * @Title: StartService
 * @ProjectName com.qingfeng
 * @Description: TODO
 * @date 2018-10-2310:21
 */
import com.qingfeng.base.model.CommonConfig;
import com.qingfeng.framework.exception.GlobalExceptionHandler;
import com.qingfeng.util.*;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 继承Application接口后项目启动时会按照执行顺序执行run方法
 * 通过设置Order的value来指定执行的顺序
 */
@Component
@Order(value = 1)
public class StartService implements ApplicationRunner {

    @Autowired
    private CommonConfig commonConfig;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
//        ClassPathResource classPathResource = new ClassPathResource("config/QingfengLicense.xml");
//        InputStream inputStream =classPathResource.getInputStream();
        File file = new File("D:\\home\\QingfengLicense.xml");
        // 创建SAXReader对象
        SAXReader reader = new SAXReader();
        // 加载xml文件
        Document dc= reader.read(file);
        // 获取根节点
        Element e = dc.getRootElement();
        System.out.println(e);
        // 获取迭代器
        Iterator it = e.elementIterator();
        // 遍历迭代器，获取根节点信息
        PageData pd = new PageData();
        while(it.hasNext()){
            Element book = (Element) it.next();
            pd.put(book.getName(),book.getStringValue());
        }
        String LicenseKeys = pd.get("LicenseKeys").toString();

        String license = RSAUtils.decrypt(LicenseKeys,commonConfig.getPrivateKey());
        if(Verify.verifyIsNull(license)){
            throw new RuntimeException("正式认证失败。");
        }else{
            System.out.println(license);
            String[] params = license.split("#", -1);
            System.out.println(params.length);
            String title = params[0];
            String start_time = params[1];
            String end_time = params[2];
            String ip_address = params[3];
            String mac_address = params[4];
            String other_limit = params[5];

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date startDate = sdf.parse(start_time);
//            Date endDate = sdf.parse(end_time);
//            if(startDate.after(DateTimeUtil.getDDate()) || endDate.before(DateTimeUtil.getDDate())){
//                throw new RuntimeException("认证正式已失效，请重新授权。");
//            }
//            System.out.println(IpUtils.getIpAddress());
//            System.out.println(IpUtils.getMacAddress());
//            if(Verify.verifyIsNotNull(ip_address)){
//                if(!ip_address.equals(IpUtils.getIpAddress())){
//                    throw new RuntimeException("授权IP无效，请重新授权。");
//                }
//            }
//            if(Verify.verifyIsNotNull(mac_address)){
//                if(!mac_address.equals(IpUtils.getMacAddress())){
//                    throw new RuntimeException("授权MAC无效，请重新授权。");
//                }
//            }

        }
//        System.out.println("###############################项目启动成功###############################");
    }
}
