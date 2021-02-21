package com.tfit.BdBiProcSrvShEduOmc.service.info.manage;

import com.tfit.BdBiProcSrvShEduOmc.model.ro.GroupMealCompanyBasicRO;

/**
 * @Descritpion：团餐公司服务
 * @author: tianfang_infotech
 * @date: 2019/1/8 10:49
 */
public interface GroupMealCompanyBasicService {
    /**
     * 根据编号从redis中查询团餐公司基础信息
     * @param schoolId
     * @return
     */
    GroupMealCompanyBasicRO getGroupMealCompanyBasicFromRedis(String schoolId);
}
