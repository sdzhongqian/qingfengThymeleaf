package com.qingfeng.common.dao;

import com.qingfeng.base.dao.CrudDao;
import com.qingfeng.util.PageData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ProjectName AuthCertDao
 * @author Administrator
 * @version 1.0.0
 * @Description AuthCertDao
 * @createTime 2021/8/11 14:32
 */
@Mapper
public interface AuthCertDao extends CrudDao<PageData> {

    /**
     * @title updateStatus
     * @description updateStatus
     * @author Administrator
     * @updateTime 2021/8/11 14:32
     */
    public void updateStatus(PageData pd);


}
