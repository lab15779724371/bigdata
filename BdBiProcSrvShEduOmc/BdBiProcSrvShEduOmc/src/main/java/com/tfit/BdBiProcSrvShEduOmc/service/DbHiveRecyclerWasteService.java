package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonRecs;

/**
 * 垃圾回收废弃油脂相关hive库的查询
 * @author Administrator
 *
 */
public interface DbHiveRecyclerWasteService {
	
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_recycler_waste中根据条件查询数据列表
	 * @param licType
	 * @param listYearMonth
	 * @param startDate
	 * @param endDateAddOne
	 * @param distId
	 * @return
	 */
    public List<KwCommonRecs> getRecyclerWasteList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,List<Object> distIdList,String supplierArea,List<Object> supplierAreaList,
    		int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
    		Integer platformType ,Integer type,Integer statMode);
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_warn_detail中根据条件查询数据列表
	 * @return
	 */
    List<KwCommonDets> getRecyclerWasteDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,String ppName, int schType, 
    		String rmcId,String rmcName, String recComany, String recPerson, int schProp,
			int subLevel, int compDep,Integer secontType,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			Integer platformType ,Integer type,
    		Integer startNum,Integer endNum);
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_warn_detail中根据条件查询数据条数
     * @return
     */
    Integer getRecyclerWasteDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,String ppName, int schType, 
    		String rmcId,String rmcName, String recComany, String recPerson, int schProp,
			int subLevel, int compDep,Integer secontType,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			Integer platformType ,Integer type);
}
