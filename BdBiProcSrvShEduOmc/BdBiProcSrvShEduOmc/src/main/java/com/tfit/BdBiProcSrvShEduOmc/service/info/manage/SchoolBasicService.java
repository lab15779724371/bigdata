package com.tfit.BdBiProcSrvShEduOmc.service.info.manage;

import com.tfit.BdBiProcSrvShEduOmc.model.ro.SchoolBasicRO;

/**
 * @Descritpion：学校基础服务
 * @author: tianfang_infotech
 * @date: 2019/1/8 10:23
 */
public interface SchoolBasicService {

    /**
     * 根据编号从redis中查询学校基础信息
     * @param schoolId
     * @return
     */
    SchoolBasicRO getSchoolBasicFromRedis(String schoolId);
}
