package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.obj.base.TBaseMaterial;

/**
 * 排菜相关hive库的查询
 * @author Administrator
 *
 */
public interface DbHiveBaseService {
    /**
    * 从数据库app_saas_v1的数据表t_base_material中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<TBaseMaterial> getTBaseMaterialList(TBaseMaterial inputObj,
    		Integer startNum,Integer endNum);
}
