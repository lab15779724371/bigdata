package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;
import java.util.Map;

import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLicDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLics;
import com.tfit.BdBiProcSrvShEduOmc.obj.warn.AppTEduNoLedgerCollectD;
import com.tfit.BdBiProcSrvShEduOmc.obj.warn.AppTEduNoPlatoonCollectD;

/**
 * 预警先关hive库的查询
 * @author Administrator
 *
 */
public interface DbHiveWarnService {
	
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_warn_total中根据条件查询数据列表
	 * @param licType
	 * @param listYearMonth
	 * @param startDate
	 * @param endDateAddOne
	 * @param distId
	 * @return
	 */
    public List<WarnCommonLics> getWarnLicList(Integer licType, List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,Integer target);
    
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_warn_level_total中根据条件查询数据列表
  	 */
    public List<WarnCommonLics> getWarnLicListByLevel(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,Integer target );
    
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_warn_nature_total中根据条件查询数据列表
  	 */
    public List<WarnCommonLics> getWarnLicListByNature(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,Integer target );
    	
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_warn_detail中根据条件查询数据列表
	 * @return
	 */
    List<WarnCommonLicDets> getWarnLicDetsList(Integer warnType,Integer certificateType,List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId, String schName, int schType, int licType, int licStatus, int licAuditStatus,List<Object> licAuditStatussList, 
			String startElimDate, String endElimDate, String startValidDate, String endValidDate, 
			int schProp, String licNo,String rmcName,String fullName,Integer target,
			String departmentId,List<Object> departmentIdList,String trigWarnUnit,String area,String warnTypeChild,String warnLevelName,
    		Integer startNum,Integer endNum,Map<Integer, String> schoolPropertyMap);
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_warn_detail中根据条件查询数据条数
     * @return
     */
    Integer getWarnLicDetsCount(Integer warnType,Integer certificateType,List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId, String schName, int schType, int licType, int licStatus, int licAuditStatus,List<Object> licAuditStatussList, 
			String startElimDate, String endElimDate, String startValidDate, String endValidDate, 
			int schProp, String licNo,String rmcName,String fullName,Integer target,String departmentId,List<Object> departmentIdList,
			String trigWarnUnit,String area,String warnTypeChild,String warnLevelName);
    
    /**
     * 
     * @param warnType
     * @param licType
     * @param target
     * @param listYearMonth
     * @param startDate
     * @param endDateAddOne
     * @param distId
     * @param distIdList
     * @param subLevel
     * @param compDep
     * @param subLevels
     * @param compDeps
     * @param departmentId
     * @param departmentIdList
     * @param statMode
     * @return
     */
    public List<WarnCommon> getWarnList(Integer warnType,Integer licType,Integer target, List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,List<Object> distIdList,
    		int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
    		String departmentId,List<Object> departmentIdList,
    		Integer statMode );
    
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_ledger_collect_d中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduNoLedgerCollectD> getAppTEduNoLedgerCollectDList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoLedgerCollectD inputObj,
    		Integer startNum,Integer endNum);
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_ledger_collect_d中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduNoLedgerCollectDListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoLedgerCollectD inputObj);
    
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_ledger_collect_d中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public AppTEduNoLedgerCollectD getAppTEduNoLedgerCollectDListSum(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoLedgerCollectD inputObj);

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_platoon_collect_d中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduNoPlatoonCollectD> getAppTEduNoPlatoonCollectDList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoPlatoonCollectD inputObj,
    		Integer startNum,Integer endNum);

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_platoon_collect_d中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduNoPlatoonCollectDListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoPlatoonCollectD inputObj);
    
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_platoon_collect_d中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public AppTEduNoPlatoonCollectD getAppTEduNoPlatoonCollectDListSum(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoPlatoonCollectD inputObj);
}
