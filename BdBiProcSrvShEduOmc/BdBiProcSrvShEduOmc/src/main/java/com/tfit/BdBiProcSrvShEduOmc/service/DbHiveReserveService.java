package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.obj.opt.AppTEduReserveTotalD;

/**
 * 排菜相关hive库的查询
 * @author Administrator
 *
 */
public interface DbHiveReserveService {
	 /**
	    * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_d中根据条件查询数据列表
	    * @param listYearMonth
	    * @param startDate
	    * @param endDateAddOne
	    * @return
	    */
	    public List<AppTEduReserveTotalD> getAppTEduReserveTotalDList(List<String> listYearMonth, String startDate,String endDateAddOne,
	    		AppTEduReserveTotalD inputObj,
	    		Integer startNum,Integer endNum);
	    /**
	     * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_d中根据条件查询数据列表个数
	     * @param listYearMonth
	     * @param startDate
	     * @param endDateAddOne
	     * @return
	     */
	     public Integer getAppTEduReserveTotalDListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
	     		AppTEduReserveTotalD inputObj);
	
}
