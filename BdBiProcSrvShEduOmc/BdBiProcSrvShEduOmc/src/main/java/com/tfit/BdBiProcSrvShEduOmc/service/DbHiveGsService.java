package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduLedgerMasterTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsCommon;

/**
 * 排菜相关hive库的查询
 * @author Administrator
 *
 */
public interface DbHiveGsService {
	
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_platoon_total中根据条件查询数据列表
	 * @param licType
	 * @param listYearMonth
	 * @param startDate
	 * @param endDateAddOne
	 * @param distId
	 * @return
	 */
    public List<SchGsCommon> getGsList(String tableName,List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,List<Object> distIdList,
    		int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
    		String departmentId,List<Object> departmentIdList,
    		Integer statMode);
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据列表
	 * @return
	 */
    List<GsPlanOptCommonDets> getGsDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp,
			String schoolId,String ppName,String rmcId, String rmcName, int schType, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			String distrBatNumber, int assignStatus, int dispStatus, int acceptStatus,
	    	List<Object> dispStatussList,List<Object> assignStatussList,int dispType, int dispMode,
	    	String departmentId,List<Object> departmentIdList,String plastatus,
	    	List<String> distrBatNumberList,
	    	int mode,
    		Integer startNum,Integer endNum);
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据条数
     * @return
     */
    Integer getGsDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp,
			String schoolId,String ppName,String rmcId, String rmcName, int schType, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			String distrBatNumber, int assignStatus, int dispStatus, int acceptStatus,
	    	List<Object> dispStatussList,List<Object> assignStatussList,int dispType, int dispMode,
	    	String departmentId,List<Object> departmentIdList,String plastatus,List<String> distrBatNumberList);
	//--------------------配货-周报表----------------------------------------------

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_ledger_master_total_w中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduLedgerMasterTotalWObj> getAppTEduLedgerMasterTotalWObjList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduLedgerMasterTotalWObj inputObj,
    		Integer startNum,Integer endNum) ;
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_ledger_master_total_w中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduLedgerMasterTotalWObjListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduLedgerMasterTotalWObj inputObj) ;
}
