package com.qingfeng.common.service;

import com.qingfeng.base.service.CrudService;
import com.qingfeng.common.dao.AuthCertDao;
import com.qingfeng.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName AuthCertService
 * @author Administrator
 * @version 1.0.0
 * @Description AuthCertService
 * @createTime 2021/8/11 14:33
 */
@Service
@Transactional
public class AuthCertService extends CrudService<AuthCertDao, PageData> {

    @Autowired
    protected AuthCertDao authCertDao;

    /**
     * @title updateStatus
     * @description updateStatus
     * @author Administrator
     * @updateTime 2021/8/11 14:33
     */
    public void updateStatus(PageData pd){
        authCertDao.updateStatus(pd);
    }

}