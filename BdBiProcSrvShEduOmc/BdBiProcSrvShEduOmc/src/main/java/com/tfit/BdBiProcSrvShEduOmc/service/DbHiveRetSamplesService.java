package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduPlatoonTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduReserveTotalW;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;

/**
 * 排菜相关hive库的查询
 * @author Administrator
 *
 */
public interface DbHiveRetSamplesService {
	
     
 	//--------------------使用情况-周报表----------------------------------------------
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_w中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduReserveTotalW> getAppTEduReserveTotalWList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduReserveTotalW inputObj,
    		Integer startNum,Integer endNum) ;
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_w中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduReserveTotalWListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduReserveTotalW inputObj);
}
