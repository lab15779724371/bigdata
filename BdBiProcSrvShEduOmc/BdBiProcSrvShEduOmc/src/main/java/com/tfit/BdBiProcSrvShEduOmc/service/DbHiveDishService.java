package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.LinkedHashMap;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduPlatoonTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;

/**
 * 排菜相关hive库的查询
 * @author Administrator
 *
 */
public interface DbHiveDishService {
	
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_platoon_total中根据条件查询数据列表
	 * @param licType
	 * @param listYearMonth
	 * @param startDate
	 * @param endDateAddOne
	 * @param distId
	 * @return
	 */
    public List<SchDishCommon> getDishList(String tableName,List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,List<Object> distIdList,
    		int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
    		String departmentId,List<Object> departmentIdList,
    		Integer statMode);
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据列表
	 * @return
	 */
    List<PpDishCommonDets> getDishDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			String ppName,String rmcId, String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			Integer acceptStatus,Integer assignStatus,Integer dispStatus,
			String departmentId,List<Object> departmentIdList,String plastatus,String reason,
			String disDealStatus,List<String> schoolList,
			int mode,
    		Integer startNum,Integer endNum);
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据条数
     * @return
     */
    Integer getDishDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			String ppName,String rmcId, String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			Integer acceptStatus,Integer assignStatus,Integer dispStatus,
			String departmentId,List<Object> departmentIdList,String plastatus,String reason,
			String disDealStatus,List<String> schoolList);
    
    //*********************排菜使用情况详情***************************************************
 	/**
 	 * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据列表
 	 * @return
 	 */
     List<PpDishCommonDets> getDishUseDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
     		String distIdorSCName,int subLevel, 
 			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
 			String schoolId,String ppName,String rmcId, String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
 			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
 			List<Object> optModesList,List<Object> subDistNamesList,
 			Integer acceptStatus,Integer assignStatus,Integer dispStatus,
 			Integer materialStatus,Integer haveReserve,String reason,
 			String departmentId,List<Object> departmentIdList,
     		Integer startNum,Integer endNum);
     /**
      * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据条数
      * @return
      */
     Integer getDishUseDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
     		String distIdorSCName,int subLevel, 
 			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
 			String schoolId,String ppName,String rmcId, String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
 			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
 			List<Object> optModesList,List<Object> subDistNamesList,
 			Integer acceptStatus,Integer assignStatus,Integer dispStatus,
 			Integer materialStatus,Integer haveReserve,String reason,
 			String departmentId,List<Object> departmentIdList);
     
 	//--------------------使用情况-周报表----------------------------------------------
     /**
     * 从数据库app_saas_v1的数据表app_t_edu_platoon_total_w中根据条件查询数据列表
     * @param listYearMonth
     * @param startDate
     * @param endDateAddOne
     * @return
     */
     public List<AppTEduPlatoonTotalWObj> getAppTEduPlatoonTotalWObjList(List<String> listYearMonth, String startDate,String endDateAddOne,
     		AppTEduPlatoonTotalWObj inputObj,
     		Integer startNum,Integer endNum);
     /**
      * 从数据库app_saas_v1的数据表app_t_edu_platoon_total_w中根据条件查询数据列表个数
      * @param listYearMonth
      * @param startDate
      * @param endDateAddOne
      * @return
      */
      public Integer getAppTEduPlatoonTotalWObjListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
      		AppTEduPlatoonTotalWObj inputObj);
     //查询排菜异常数据
     List<AppCommonDao> getSendSteakDataAnomalyWarning(String startDate,String endDate,float offset,String supply_date,String departmentId);
     //查询验收异常数据
     List<AppCommonDao> getSendAcceptanceDataAnomalyWarning(String startDate,String endDate,String deliveryWarn,String use_date,String departmentId);
}
