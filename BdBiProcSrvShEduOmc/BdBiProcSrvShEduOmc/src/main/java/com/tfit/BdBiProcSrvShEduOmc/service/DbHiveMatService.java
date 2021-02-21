package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpMatCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduMaterialTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchMatCommon;

/**
 * 用料相关hive库的查询
 * @author Administrator
 *
 */
public interface DbHiveMatService {
	
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_material_total中根据条件查询数据列表
	 * @param licType
	 * @param listYearMonth
	 * @param startDate
	 * @param endDateAddOne
	 * @param distId
	 * @return
	 */
    public List<SchMatCommon> getMatList(String tableName,List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,List<Object> distIdList,
    		int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
    		String departmentId,List<Object> deparmentIds,
    		Integer statMode);
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_material_detail中根据条件查询数据列表
	 * @return
	 */
    List<PpMatCommonDets> getMatDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,String ppName, String rmcId,String rmcName, 
			int subLevel, int compDep, int schGenBraFlag, String subDistName, int fblMb, 
			int confirmFlag, int schType, int schProp, int optMode, int matType,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			String departmentId,List<Object> deparmentIds,
    		Integer startNum,Integer endNum);
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_material_detail中根据条件查询数据条数
     * @return
     */
    Integer getMatDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,String ppName, String rmcId,String rmcName, 
			int subLevel, int compDep, int schGenBraFlag, String subDistName, int fblMb, 
			int confirmFlag, int schType, int schProp, int optMode, int matType,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			String departmentId,List<Object> deparmentIds);
	//--------------------用料-周报表----------------------------------------------

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_material_total_w中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduMaterialTotalWObj> getAppTEduMaterialTotalWObjList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduMaterialTotalWObj inputObj,
    		Integer startNum,Integer endNum);
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_material_total_w中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduMaterialTotalWObjListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduMaterialTotalWObj inputObj) ;
}
